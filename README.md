# dact: a DTO-Pact generator

**dact** is a Java library/project that allows you to generate [Pact](https://docs.pact.io/) contracts directly from your Data Transfer Objects (DTOs). Using **dact**, you can define DTOs with concrete values and flexible matchers, then automatically produce Pact contracts for consumer-driven contract testing.

---

## Features

- Generate Pact contracts directly from Java DTOs.
- Supports nested DTOs.
- Flexible matching with `stringType`, `integerType`, `regex`, and other matchers.
- Simplifies consumer-driven contract testing.
- Easy integration with existing DTO-based projects.

---

## Installation

Include the following dependency in your `pom.xml` (or adjust for Gradle):

```xml
<dependency>
    <groupId>com.github.pfichtner</groupId>
    <artifactId>dact</artifactId>
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
import static com.github.pfichtner.dact.DTOPactContract.contractFor;
import static com.github.pfichtner.dact.PactMatchers.integerType;
import static com.github.pfichtner.dact.PactMatchers.regex;
import static com.github.pfichtner.dact.PactMatchers.stringType;
import static com.github.pfichtner.dact.PactDslBuilderFromDTO.buildDslFrom;

PersonDTO person = contractFor(new PersonDTO())
    .givenname(regex("G.*", "Givenname1"))
    .lastname(regex("L.*", "Lastname1"))
    .age(integerType(42))
    .address(
        contractFor(new AddressDTO())
            .zip(integerType(12345))
            .city(stringType("City"))
    );

PactDslJsonBody pactBody = buildDslFrom(person);
```

---

## Matchers

Dact supports flexible matchers to make your contracts robust:

- `stringType("example")` – Matches any string.
- `integerType(42)` – Matches any integer.
- `regex("G.*", "Givenname1")` – Matches strings with a regex pattern.

Nested DTOs are supported via `contractFor(nestedDTO)`.

---

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

---

