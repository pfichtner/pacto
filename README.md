# <img src="https://pfichtner.github.io/pacto/pacto.jpg" align="right" width="100">pacto: a DTO-Pact generator

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

Contract testing with Pact is powerful—but writing and maintaining Pact DSLs by hand can be repetitive and error-prone. pacto solves this by generating contracts directly from your DTOs.

Key benefits:
- 🔄 No duplication – Contracts are generated from the same DTOs you already use.
- 🛡️ Always in sync – DTO changes automatically flow into contracts, reducing drift.
- ⚡ Less boilerplate – No more hand-writing verbose Pact DSL code.
- 🎯 Robust matchers – Define flexible rules (regex, stringType, etc.) for realistic contracts.
- 🧩 Supports complex models – Works seamlessly with nested DTOs.
- 🚀 Easy adoption – Integrates into existing Java projects with minimal setup.

With pacto, you get reliable consumer-driven contract tests with less effort and fewer mistakes.

## Installation

Include the following dependency in your `pom.xml` (or adjust for Gradle):

```xml
<dependency>
    <groupId>com.github.pfichtner</groupId>
    <artifactId>pacto</artifactId>
    <version>0.0.1</version>
</dependency>
```

---

## Usage

### 1. Define your DTO (usually already done)

```java
public class PersonDTO {
    private String givenname;
    private String lastname;
    private int age;
    private AddressDTO address;

    // Getters and setters omitted for brevity
}

public class AddressDTO {
    private int zip;
    private String city;

    // Getters and setters omitted for brevity
}
```

### 2. Create a Pact contract from a DTO

```java
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.PactoDslBuilder.buildDslFrom;
import static com.github.pfichtner.pacto.matchers.Matchers.*;

PersonDTO person = spec(new PersonDTO())
    .givenname(regex("G.*", "Givenname1"))
    .lastname(regex("L.*", "Lastname1"))
    .age(integerType(42))
    .address(
        spec(new AddressDTO())
            .zip(integerType(12345))
            .city(stringType("City"))
    );

PactDslJsonBody pactBody = buildDslFrom(person);
```

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

