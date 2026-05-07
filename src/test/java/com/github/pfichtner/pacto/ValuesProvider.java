package com.github.pfichtner.pacto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

public class ValuesProvider implements org.junit.jupiter.params.provider.ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(org.junit.jupiter.api.extension.ExtensionContext context) {
        return Stream.of(
                Arguments.arguments(int.class, 42, "42"),
                Arguments.arguments(Integer.class, 42, "42"),
                Arguments.arguments(long.class, 42L, "42"),
                Arguments.arguments(Long.class, 42L, "42"),
                Arguments.arguments(double.class, 42.0d, "42.0"),
                Arguments.arguments(Double.class, 42.0d, "42.0"),
                Arguments.arguments(float.class, 42.0f, "42.0"),
                Arguments.arguments(Float.class, 42.0f, "42.0"),
                Arguments.arguments(boolean.class, true, "true"),
                Arguments.arguments(Boolean.class, true, "true"),
                Arguments.arguments(BigDecimal.class, BigDecimal.valueOf(42), "42"),
                Arguments.arguments(BigDecimal.class, BigDecimal.valueOf(42.0d), "42.0"),
                Arguments.arguments(BigInteger.class, BigInteger.valueOf(42), "42"),
                Arguments.arguments(Date.class, new Date(0), "\"2000-01-31\""),
                Arguments.arguments(LocalDate.class, LocalDate.of(2001, 2, 3), "\"2000-01-31\"")
        );
    }
}
