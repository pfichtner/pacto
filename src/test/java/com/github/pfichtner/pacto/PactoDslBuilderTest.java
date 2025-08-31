package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.PactoDslBuilder.appendInvocations;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatcher;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

class PactoDslBuilderTest {

	private final class InvocationStub implements Invocation {

		private final String attribute;
		private final Class<?> type;
		private final Object arg;

		public InvocationStub(Class<?> type, Object value) {
			this.attribute = "testAttribute";
			this.type = type;
			this.arg = value;
		}

		@Override
		public ArgumentMatcher<?> getMatcher() {
			return null;
		}

		@Override
		public Object getArg() {
			return arg;
		}

		@Override
		public Method getMethod() {
			return null;
		}

		@Override
		public Object getDelegate() {
			return null;
		}

		@Override
		public String getAttribute() {
			return attribute;
		}

		@Override
		public Class<?> getType() {
			return type;
		}
	}

	@ParameterizedTest
	@MethodSource("values")
	void test(Class<?> type, Object value, String expected) {
		assertThat(callSut(new InvocationStub(type, value))).hasToString("{\"testAttribute\":" + expected + "}");
	}

	private static List<Arguments> values() {
		return List.of( //
				arguments(int.class, 42, "42"), //
				arguments(Integer.class, 42, "42"), //
				arguments(long.class, 42L, "42"), //
				arguments(Long.class, 42L, "42"), //
				arguments(double.class, 42.0d, "42.0"), //
				arguments(Double.class, 42.0d, "42.0"), //
				arguments(float.class, 42.0f, "42.0"), //
				arguments(Float.class, 42.0f, "42.0"), //
				arguments(boolean.class, true, "true"), //
				arguments(Boolean.class, true, "true"), //
				arguments(BigDecimal.class, BigDecimal.valueOf(42), "42"), //
				arguments(BigDecimal.class, BigDecimal.valueOf(42.0d), "42.0"), //
				arguments(BigInteger.class, BigInteger.valueOf(42), "42") //
		);
	}

	private DslPart callSut(InvocationStub e1) {
		return appendInvocations(new PactDslJsonBody(), List.of(e1));
	}

}
