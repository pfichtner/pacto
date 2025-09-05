package com.github.pfichtner.pacto.matchers;

import static com.github.pfichtner.pacto.Pacto.invocations;
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.decimalType;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.integerType;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.maxArrayLike;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.minArrayLike;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.numberType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import com.github.pfichtner.pacto.matchers.TestInputDataProvider.TestInputData;
import com.github.pfichtner.pacto.matchers.PactoMatchers.Lists;
import com.github.pfichtner.pacto.matchers.PactoMatchers.Sets;
import com.github.pfichtner.pacto.testdata.Bar;
import com.github.pfichtner.pacto.testdata.Foo;

class PactoMatchersTest {

	TestTarget target = new TestTarget();

	@Test
	@PostCleanMatcherStack
	void canCompilePrimitives() {
		target.floatArg(decimalType());
		target.floatArg(decimalType(1.23F));
		target.floatWrapperArg(decimalType());
		target.floatWrapperArg(decimalType(1.23F));
		target.doubleArg(decimalType());
		target.doubleArg(decimalType(1.23));
		// TODO fix to get rid of cast
		target.doubleWrapperArg((double) decimalType());
		target.doubleWrapperArg(decimalType(1.23D));
		target.intArg(integerType());
		target.intArg(integerType(42));
		target.integerWrapperArg(integerType());
		target.integerWrapperArg(integerType(42));
		target.longArg(integerType());
		target.longArg(integerType(42L));
		// TODO fix to get rid of cast
		target.longWrapperArg((long) integerType());
		target.longWrapperArg(integerType(42L));
	}

	@Test
	@PostCleanMatcherStack
	void canCompileNumberArg() {
		target.intArg(numberType(42));
		target.intArg(numberType(Integer.valueOf(42)));
		target.longArg(numberType(42L));
		target.longArg(numberType(Long.valueOf(42)));
		target.floatArg(numberType(Float.valueOf(1.23F)));
		target.floatArg(numberType(1.23F));
		target.doubleArg(numberType(Double.valueOf(1.23D)));
		target.doubleArg(numberType(1.23D));
		target.numberArg(numberType(BigInteger.valueOf(123)));
		target.numberArg(numberType(new BigDecimal(123)));
	}

	@Test
	@PostCleanMatcherStack
	void canCompileCollections() {
		target.arrayArg(minArrayLike(new Foo(), 1));
		target.arrayArg(maxArrayLike(new Foo(), 2));
		target.listArg(Lists.minArrayLike(new Foo(), 3));
		target.listArg(Lists.maxArrayLike(new Foo(), 4));
		target.setArg(Sets.minArrayLike(new Foo(), 5));
		target.setArg(Sets.maxArrayLike(new Foo(), 6));
	}

	@Test
	void testMinArrayLike() {
		Foo foo = spec(new Foo());
		int min = 101;
		foo.setBars1(minArrayLike(spec(new Bar()).value("array-min"), min));
		foo.setBars2(Lists.minArrayLike(spec(new Bar()).value("list-min"), min));
		foo.setBars3(Sets.minArrayLike(spec(new Bar()).value("set-min"), min));
		assertThat(invocations(foo).invocations()).hasSize(3).allSatisfy(i -> assertThat(i.matcher()) //
				.isInstanceOfSatisfying(EachLikeArg.class, m -> assertSoftly(s -> { //
					s.assertThat(m.toString()).startsWith("minArrayLike(").contains(String.valueOf(min));
					s.assertThat(m.min()).isEqualTo(min);
				})));
	}

	@Test
	void testMaxArrayLike() {
		Foo foo = spec(new Foo());
		int max = 102;
		foo.setBars1(maxArrayLike(spec(new Bar()).value("array-max"), max));
		foo.setBars2(Lists.maxArrayLike(spec(new Bar()).value("list-max"), max));
		foo.setBars3(Sets.maxArrayLike(spec(new Bar()).value("set-max"), max));
		assertThat(invocations(foo).invocations()).hasSize(3).allSatisfy(i -> assertThat(i.matcher()) //
				.isInstanceOfSatisfying(EachLikeArg.class, m -> assertSoftly(s -> { //
					s.assertThat(m.toString()).startsWith("maxArrayLike(").contains(String.valueOf(max));
					s.assertThat(m.max()).isEqualTo(max);
				})));
	}

	@ParameterizedTest
	@ArgumentsSource(value = TestInputDataProvider.class)
	void testAll(TestInputData<?> testInputData) {
		TestTarget spec = testInputData.handle(spec(target));
		Object value = testInputData.in();
		assertThat(invocations(spec).invocations()).singleElement() //
				.satisfies(i -> assertThat(i.matcher()) //
						.isInstanceOfSatisfying(testInputData.type(), m -> assertSoftly(s -> { //
							s.assertThat(m.value()).isEqualTo(value);
							s.assertThat(m).hasToString(testInputData.toStringFormat(), value);
						})));
	}

}
