# <img src="https://pfichtner.github.io/pacto/pacto.jpg" align="right" width="100">pacto: a DTO-Pact generator


[![Java CI with Maven](https://github.com/pfichtner/pacto/actions/workflows/maven.yml/badge.svg)](https://github.com/pfichtner/pacto/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.pfichtner/pacto.svg)](https://search.maven.org/artifact/io.github.pfichtner/pacto)
[![Pact](https://img.shields.io/badge/Pact-JVM-blue?logo=pact)](https://docs.pact.io/)

Generate [Pact](https://docs.pact.io/) contracts directly from your DTOs — keep tests simple, reliable, and always in sync.

**pacto** is a Java library/project that allows you to generate [Pact](https://docs.pact.io/) contracts directly from your Data Transfer Objects (DTOs). Using **pacto**, you can define DTOs with concrete values and flexible matchers, then automatically produce Pact contracts for consumer-driven contract testing.

---

## Features

- Generate [Pact](https://docs.pact.io/) contracts directly from Java DTOs.
- Supports nested DTOs.
- Flexible matching with `stringType`, `integerType`, `regex`, and other matchers.
- Simplifies consumer-driven contract testing.
- Easy integration with existing DTO-based projects.

---

## Why use pacto?  

Contract testing with [Pact](https://docs.pact.io/) is powerful—but writing and maintaining Pact DSLs by hand can be repetitive and error-prone. **pacto** solves this by generating contracts directly from your DTOs.  

**Key benefits:**  
- 🔄 **No duplication** – Contracts are generated from the same DTOs you already use.  
- 🛡️ **Always in sync** – DTO changes automatically flow into contracts, reducing drift.  
- ⚡ **Less boilerplate** – No more hand-writing verbose Pact DSL code.  
- 🎯 **Robust matchers** – Define flexible rules (`regex`, `stringType`, etc.) for realistic contracts.  
- 🧩 **Supports complex models** – Works seamlessly with nested DTOs.  
- 🚀 **Easy adoption** – Integrates into existing Java projects with minimal setup.  

With **pacto**, you get reliable consumer-driven contract tests powered by [Pact](https://docs.pact.io/) with less effort and fewer mistakes.  

---

## The problem: duplication without pacto

```java
// DTO definitions — the single source of truth for both examples
@Data
@Accessors(chain = true, fluent = true)
public class PersonDTO {
    private String givenname;
    private String lastname;
    private int age;
    private AddressDTO address;
}

@Data
@Accessors(chain = true, fluent = true)
public class AddressDTO {
    private int zip;
    private String city;
}
```

Without pacto, you need to define your data structures **twice** — once in your DTO, and once again in Pact’s DSL.  

**❌ Manual Pact DSL (with duplication):**  

```java
// And again in Pact DSL:
RequestResponsePact pact = ConsumerPactBuilder
    .consumer("SomeConsumer")
    .hasPactWith("SomeProvider")
    .uponReceiving("POST /person")
    .path("/person")
    .method("POST")
    .body(new PactDslJsonBody()
        .stringMatcher("givenname", "G.*", "Givenname1")
        .stringMatcher("lastname", "L.*", "Lastname1")
        .integerType("age", 42)
        .object("address")
            .integerType("zip", 12345)
            .stringType("city", "City")
        .closeObject()
    )
    .willRespondWith()
    .status(200)
    .body(new PactDslJsonBody()
        .stringType("givenname", "Givenname1")
        .stringType("lastname", "Lastname1")
        .integerType("age", 42)
        .object("address")
            .integerType("zip", 12345)
            .stringType("city", "City")
        .closeObject()
    )
    .toPact();
```

If your DTO changes (e.g., adding `country` to `AddressDTO`), you must update both places or your contract drifts out of sync.  

**✅ With pacto (no duplication):**  

```java
RequestResponsePact pact = ConsumerPactBuilder
    .consumer("SomeConsumer")
    .hasPactWith("SomeProvider")
    .uponReceiving("POST /person")
    .path("/person")
    .method("POST")
    .body(
        dslFrom(
            spec(new PersonDTO())
                .givenname(stringMatcher("G.*", "Givenname1"))
                .lastname(stringMatcher("L.*", "Lastname1"))
                .age(integerType(42))
                .address(
                    spec(new AddressDTO())
                        .zip(integerType(12345))
                        .city(stringType("City"))
                )
        )
    )
    .willRespondWith()
    .status(200)
    .body(
        dslFrom(
            spec(new PersonDTO())
                .givenname(stringType("Givenname1"))
                .lastname(stringType("Lastname1"))
                .age(integerType(42))
                .address(
                    spec(new AddressDTO())
                        .zip(integerType(12345))
                        .city(stringType("City"))
                )
        )
    )
    .toPact();
```

Now, your contract is generated **directly from the DTO** — no duplication, no drift, no extra maintenance.  
No DTO duplication → single source of truth.
- Compile-time safety; fewer typos.
- Nested objects & arrays handled automatically.
- Auto-update contracts when DTO changes.
- Concise matcher syntax.
- Reusable and composable matchers.
- Reduced risk of contract drift.

With pacto, you avoid duplication, reduce boilerplate, and ensure your contracts stay in sync with your DTOs.

## How to use pacto?

pacto is available on Maven Central. You can include it as a dependency:

```xml
<dependency>
  <groupId>io.github.pfichtner</groupId>
  <artifactId>pacto</artifactId>
  <version>0.0.4</version>
</dependency>
```

--- 

## Matchers

pacto supports a rich set of matchers to make your contracts robust and expressive.  

> **Standing on the shoulders of giants:** pacto more or less acts as **syntax sugar** (though technically it has to capture the arguments passed to the matcher static methods) and delegates to the [Pact JVM matchers](https://docs.pact.io/implementation_guides/jvm/consumer) under the hood. You benefit from the full power and documentation of Pact itself.

- `nullValue()` – Matches a null value.
- `equalsTo(value)` – Match exact value
- `id(int|long)` – Matches an ID (special alias for integer types)
- `stringType()` / `stringType("example")` – Matches any string.
- `stringMatcher("regex", "example")` – Matches strings with a regex pattern.
- `includeStr("example")` – Matches strings that include the given substring.
- `integerType()` / `integerType(int|long)` – Matches any integer number type.
- `decimalType()` / `decimalType(double|float)` – Matches any floating point number type.
- `numberType(Number)` – Matches any number type.
- `booleanType()` / `booleanType(boolean)` – Matches any boolean.
- `booleanValue(boolean)` – Matches a specific boolean value.
- `hex()` / `hex(String)` – Matches any hex value.
- `uuid()` / `uuid(String|UUID)` – Matches any UUID.
- `time(("format"), LocalDateTime|Date)` - Matches times given the format
- `date(("format"), LocalDate|Date)` - Matches dates given the format
- `datetime(("format",) LocalDate|Date)` - Matches datetimes given the format
- `ipAddress()` – Matches any IP address
- `matchUrl("basepath"(, "fragments"))` – Matches URL structures
- `eachLike(value)` – Matches an array with at least one element like `value`.
- `minArrayLike(value, min)` – Array with at least `min` elements like `value`.
- `maxArrayLike(value, max)` – Array with at most `max` elements like `value`.

---

## Disadvantages / Drawbacks

While **pacto** simplifies contract generation, there are a few considerations:  

- ⚠️ **Unsupported field types:** Most common fields (strings, numbers, booleans, nested DTOs) are handled automatically. For custom or complex types, you may need to provide manual mapping or custom matcher logic.  
- ⚠️ **Manual updates for special cases:** If a DTO contains unsupported fields and you change them, you'll need to adjust the contract manually.  
- ⚠️ **Additional abstraction:** pacto introduces an extra layer on top of Pact JVM. While convenient, this means debugging or understanding matcher behavior may require looking at both pacto and underlying Pact documentation.  
- ⚠️ **Learning curve:** Users need to understand the pacto DSL and its mapping conventions, even if they are already familiar with Pact JVM.  

In most standard scenarios, these drawbacks are minor compared to the benefits of **avoiding duplication, reducing boilerplate, and keeping contracts in sync with DTOs**.


---


## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

---

