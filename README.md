# <img src="https://pfichtner.github.io/pacto/pacto.jpg" align="right" width="100">pacto: a DTO-Pact generator
Generate Pact contracts directly from your DTOs — keep tests simple, reliable, and always in sync.

**pacto** is a Java library/project that allows you to generate [Pact](https://docs.pact.io/) contracts directly from your Data Transfer Objects (DTOs). Using **pacto**, you can define DTOs with concrete values and flexible matchers, then automatically produce Pact contracts for consumer-driven contract testing.

---

## Features

- Generate Pact contracts directly from Java DTOs.
- Supports nested DTOs.
- Flexible matching with `stringType`, `integerType`, `regex`, and other matchers.
- Simplifies consumer-driven contract testing.
- Easy integration with existing DTO-based projects.

---

## Why use pacto?  

Contract testing with Pact is powerful—but writing and maintaining Pact DSLs by hand can be repetitive and error-prone. **pacto** solves this by generating contracts directly from your DTOs.  

**Key benefits:**  
- 🔄 **No duplication** – Contracts are generated from the same DTOs you already use.  
- 🛡️ **Always in sync** – DTO changes automatically flow into contracts, reducing drift.  
- ⚡ **Less boilerplate** – No more hand-writing verbose Pact DSL code.  
- 🎯 **Robust matchers** – Define flexible rules (`regex`, `stringType`, etc.) for realistic contracts.  
- 🧩 **Supports complex models** – Works seamlessly with nested DTOs.  
- 🚀 **Easy adoption** – Integrates into existing Java projects with minimal setup.  

With **pacto**, you get reliable consumer-driven contract tests with less effort and fewer mistakes.  

---

## The problem: duplication without pacto  

Without pacto, you need to define your data structures **twice** — once in your DTO, and once again in Pact’s DSL.  

**❌ Manual Pact DSL (with duplication):**  

```java
public class PersonDTO {
    private String givenname;
    private String lastname;
    private int age;
    private AddressDTO address;
    // getters/setters
}

public class AddressDTO {
    private int zip;
    private String city;
    // getters/setters
}

// And again in Pact DSL:
DslPart body = new PactDslJsonBody()
    .stringMatcher("givenname", "G.*", "Givenname1")
    .stringMatcher("lastname", "L.*", "Lastname1")
    .integerType("age", 42)
    .object("address")
        .integerType("zip", 12345)
        .stringType("city", "City")
    .closeObject();
```

If your DTO changes (e.g., adding `country` to `AddressDTO`), you must update both places or your contract drifts out of sync.  

**✅ With pacto (no duplication):**  

```java
DslPart body = pactFrom(spec(new PersonDTO())
    .givenname(stringMatcher("G.*", "Givenname1"))
    .lastname(stringMatcher("L.*", "Lastname1"))
    .age(integerType(42))
    .address(
        spec(new AddressDTO())
            .zip(integerType(12345))
            .city(stringType("City"))
    ));
```

Now, your contract is generated **directly from the DTO** — no duplication, no drift, no extra maintenance.  

---

## Matchers

pacto supports flexible matchers to make your contracts robust:

- `stringType("example")` – Matches any string.
- `integerType(42)` – Matches any integer.
- `regex("G.*", "Givenname1")` – Matches strings with a regex pattern.

Nested DTOs are supported via `spec(nestedDTO)`.

---

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

---

