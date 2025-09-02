package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.DslPartAssert.assertThatDslPart;
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.PactoDslBuilder.appendInvocations;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.stringType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatcher;

import com.github.pfichtner.pacto.matchers.EachLikeArg;
import com.github.pfichtner.pacto.matchers.HexArg;
import com.github.pfichtner.pacto.testdata.Bar;
import com.github.pfichtner.pacto.testdata.Foo;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

class PactoDslBuilderTest {

	private final class InvocationStub implements Invocation {

		private final String attribute;
		private final Class<?> type;
		private final Object arg;
		private ArgumentMatcher<?> matcher;

		public InvocationStub(Class<?> type, Object value) {
			this.attribute = "testAttribute";
			this.type = type;
			this.arg = value;
		}

		public InvocationStub withMatcher(ArgumentMatcher<?> matcher) {
			this.matcher = matcher;
			return this;
		}

		@Override
		public ArgumentMatcher<?> matcher() {
			return matcher;
		}

		@Override
		public Object arg() {
			return arg;
		}

		@Override
		public Method method() {
			return null;
		}

		@Override
		public Object delegate() {
			return null;
		}

		@Override
		public String attribute() {
			return attribute;
		}

		@Override
		public Class<?> type() {
			return type;
		}
	}

	@ParameterizedTest
	@MethodSource("values")
	void test(Class<?> type, Object value, String expected) {
		assertThat(callSut(new InvocationStub(type, value))).hasToString("{\"testAttribute\":" + expected + "}");
	}

	@Test
	void testMin() {
		int min = 101;
		EachLikeArg matcher = new EachLikeArg(spec(new Bar()).value(stringType("min"))).min(min);
		InvocationStub invocation = new InvocationStub(Foo.class, new Foo()).withMatcher(matcher);
		PactDslJsonBody expected = new PactDslJsonBody().minArrayLike(invocation.attribute(), min,
				new PactDslJsonBody().stringType("value", "min"));
		assertThatDslPart(callSut(invocation)).isEqualToDslPart(expected);
	}

	@Test
	void testMax() {
		int max = 102;
		EachLikeArg matcher = new EachLikeArg(spec(new Bar()).value(stringType("max"))).max(max);
		InvocationStub invocation = new InvocationStub(Foo.class, new Foo()).withMatcher(matcher);
		PactDslJsonBody expected = new PactDslJsonBody().maxArrayLike(invocation.attribute(), max,
				new PactDslJsonBody().stringType("value", "max"));
		assertThatDslPart(callSut(invocation)).isEqualToDslPart(expected);
	}

	@Test
	void testHex() {
		HexArg matcher = new HexArg("0000FFFF");
		InvocationStub invocation = new InvocationStub(Foo.class, new Foo()).withMatcher(matcher);
		PactDslJsonBody expected = new PactDslJsonBody().hexValue(invocation.attribute(), "0000FFFF");
		assertThatDslPart(callSut(invocation)).isEqualToDslPart(expected);
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

	private DslPart callSut(InvocationStub invocation) {
		return appendInvocations(new PactDslJsonBody(), List.of(invocation));
	}

}
