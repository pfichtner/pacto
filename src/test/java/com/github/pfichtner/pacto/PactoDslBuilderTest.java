package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.ApprovalsHelper.toJson;
import static com.github.pfichtner.pacto.DslPartAssert.assertThatDslPart;
import static com.github.pfichtner.pacto.InvocationStub.invocation;
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.PactoDslBuilder.appendInvocations;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.stringType;
import static org.approvaltests.Approvals.settings;
import static org.approvaltests.JsonApprovals.verifyAsJson;
import static org.approvaltests.namer.NamerFactory.withParameters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.approvaltests.namer.NamedEnvironment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.pfichtner.pacto.matchers.EachLikeArg;
import com.github.pfichtner.pacto.matchers.TestInputDataProvider;
import com.github.pfichtner.pacto.matchers.TestInputDataProvider.TestInputData;
import com.github.pfichtner.pacto.testdata.Bar;
import com.github.pfichtner.pacto.testdata.Foo;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

class PactoDslBuilderTest {

	@ParameterizedTest
	@MethodSource("values")
	void test(Class<?> type, Object value, String expected) {
		assertThat(callSut(invocation(value))).hasToString("{\"testAttribute\":%s}".formatted(expected));
	}

	@ParameterizedTest
	@MethodSource("values")
	void test2(Class<?> type, Object value, String expected) {
		settings().allowMultipleVerifyCallsForThisClass();
		settings().allowMultipleVerifyCallsForThisMethod();
		try (NamedEnvironment env = withParameters(type, value)) {
			verifyAsJson(toJson(callSut(invocation(value))));
		}
	}

	@Test
	void testMin() {
		int min = 101;
		String value = "min";
		EachLikeArg matcher = new EachLikeArg(spec(new Bar()).value(stringType(value))).min(min);
		var invocation = invocation(new Foo()).withMatcher(matcher);
		PactDslJsonBody expected = new PactDslJsonBody().minArrayLike(invocation.attribute(), min,
				new PactDslJsonBody().stringType("value", value));
		assertThatDslPart(callSut(invocation)).isEqualToDslPart(expected);
		assertThat(matcher).hasToString("minArrayLike(Bar(value=%s), %d)".formatted(value, min));
	}

	@Test
	void testMax() {
		int max = 102;
		String value = "max";
		EachLikeArg matcher = new EachLikeArg(spec(new Bar()).value(stringType(value))).max(max);
		var invocation = invocation(new Foo()).withMatcher(matcher);
		PactDslJsonBody expected = new PactDslJsonBody().maxArrayLike(invocation.attribute(), max,
				new PactDslJsonBody().stringType("value", value));
		assertThatDslPart(callSut(invocation)).isEqualToDslPart(expected);
		assertThat(matcher).hasToString("maxArrayLike(Bar(value=%s), %d)".formatted(value, max));
	}

	@Test
	void testMinMax() {
		int min = 103;
		int max = 104;
		String value = "min-max";
		EachLikeArg matcher = new EachLikeArg(spec(new Bar()).value(stringType(value))).min(min).max(max);
		var invocation = invocation(new Foo()).withMatcher(matcher);
		PactDslJsonBody expected = new PactDslJsonBody().minMaxArrayLike(invocation.attribute(), min, max,
				new PactDslJsonBody().stringType("value", value));
		assertThatDslPart(callSut(invocation)).isEqualToDslPart(expected);
		assertThat(matcher).hasToString("minMaxArrayLike(Bar(value=%s), %d, %d)".formatted(value, min, max));
	}

	@ParameterizedTest
	@ArgumentsSource(value = TestInputDataProvider.class)
	void testMatchers(TestInputData<?> testInputData) throws Exception {
		var invocation = invocation(new Foo()).withMatcher(testInputData.matcher());
		PactDslJsonBody expected = testInputData.handle(new PactDslJsonBody(), invocation.attribute());
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
				arguments(BigInteger.class, BigInteger.valueOf(42), "42"), //
				arguments(Date.class, new Date(0), "\"2000-01-31\""), //
				arguments(LocalDate.class, LocalDate.of(2001, 2, 3), "\"2000-01-31\"") //
//				arguments(LocalTime.class, LocalTime.of(3, 4, 5, 6), "\"T14:00:00\""), //
//				arguments(LocalDateTime.class, LocalDateTime.of(1999, 1, 2, 3, 4, 5, 6), "\"T14:00:00\"") //
		);
	}

	private DslPart callSut(Invocation invocation) {
		return appendInvocations(new PactDslJsonBody(), List.of(invocation), new PactoSettingsImpl());
	}

}
