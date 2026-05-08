# ADR-001: Support Java Records via Byte Buddy Agent

**Status:** Accepted

**Date:** 2026-05-08

## Context

Pacto generates Pact contracts by creating Byte Buddy subclass proxies of DTOs. Method calls on the proxy are delegated to the real DTO and recorded, along with any matcher arguments, to build a Pact DSL.

Java records (introduced in JDK 16) are widely used as value objects / DTOs. However, records are implicitly `final`, which prevents Byte Buddy from creating subclasses. The README previously stated that records and final classes could not be proxied, with a note that JVM instrumentation or a Java agent could theoretically enable this.

We needed a solution that allows records to be used transparently with `spec()`, `isSpec()`, `delegate()`, and `invocations()`.

## Decision

We support records (and other final classes) using a two-tier approach:

### Tier 1: Agent-based pre-load transformation (primary path)

- A Java agent is installed via `ByteBuddyAgent.install()` in `Pacto`'s static initializer.
- An `AgentBuilder.Default` registers a `ClassFileTransformer` on the `Instrumentation` that matches all classes (`type(any())`).
- When a final class (such as a record) is loaded, the transformer fires and removes the `ACC_FINAL` flag from the class bytecode **before** the JVM defines the class.
- The class is loaded as non-final, allowing Byte Buddy to create a subclass proxy via the standard `subclass()` → `defineConstructor()` → `Advice` pipeline.
- The proxy implements `HasDelegate` and `HasRecorder` interfaces, identical to non-final DTO proxies.

### Tier 2: Identity-map fallback (for already-loaded classes)

- If a final class was loaded before the agent was installed, the primary path is unavailable (JVM `Instrumentation.retransformClasses()` cannot change class modifiers).
- An `IdentityHashMap<Object, Recorder>` stores per-instance recorders for such cases.
- `spec()` returns the original instance (no proxy subclass).
- `isSpec()` and `recorder()` check the map; `delegate()` returns the original instance.

### Key implementation details

- **`IdentityHashMap` vs `WeakHashMap`**: Records have structural `equals()` (all fields are compared). A `WeakHashMap.containsKey()` would incorrectly match different record instances with the same field values. `IdentityHashMap` uses reference identity (`==`), which is correct.
- **`Reflections.copyFields()`**: Keeps the existing behavior of skipping `final` and `static` fields. This is correct because the delegation pattern means all method calls resolve through the real record instance.
- **Constructor handling**: Records have only the canonical constructor. Byte Buddy calls it with `null` arguments via `MethodCall.invoke(superConstructor).with((Object[]) new Object[superConstructor.getParameterCount()])`. The proxy's own field values are null, but delegation ensures correct behavior.

### Why not other approaches considered

- **Direct `Instrumentation.retransformClasses()`**: The JVM does not allow changing class modifiers via retransformation (`UnsupportedOperationException: class redefinition failed: attempted to change the class modifiers`).
- **`Unsafe`-based modifier modification**: Modifying the `Class` object's `modifiers` field via `Unsafe` can fool reflection but does not affect the JVM's internal `Klass` structure used for verification during subclass loading.
- **Method-level instrumentation only**: Weaving advice directly into record methods (without subclassing) would require external recorder storage and modify behavior for ALL instances of the class globally. This is more invasive and offers no advantage over the current approach.
- **No-op `spec()` return**: Simply returning the original instance without any tracking would break `isSpec()`, `delegate()`, and `invocations()` contracts.

## Consequences

### Positive

- Records can be used transparently as DTOs with `spec()`.
- No API changes — `spec()`, `isSpec()`, `delegate()`, and `invocations()` work identically for records and regular classes.
- The agent is automatically installed; no user configuration or CLI flags needed.
- Backward compatible — all 128 existing tests pass without modification.
- Non-record final classes benefit from the same mechanism.

### Negative

- The `byte-buddy-agent` dependency is added (~50KB) to the compile classpath.
- `ByteBuddyAgent.install()` dynamically attaches a Java agent at runtime, which requires the `java.instrument` module (present on all standard JDKs).
- The `AgentBuilder` transformer with `type(any())` fires for every class load. The performance impact is minimal (a single `isFinal()` check) but non-zero.
- For classes loaded before `Pacto`'s static initializer runs (edge case), the fallback path returns the original instance without a subclass proxy, meaning no method interception occurs.

### Notes

- Record accessor methods take no arguments, so they produce no `Invocation` entries (the recording mechanism tracks method *arguments*, not return values). Records are primarily useful as nested value objects passed to setter methods of other DTOs.
- The `@Disabled` test `canInterceptClassWithNoNoArgConstructor` remains disabled — constructor interception for already-compiled classes is not supported and requires compile-time instrumentation.
