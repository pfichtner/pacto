package com.github.pfichtner.pacto.matchers;

import static com.github.pfichtner.pacto.Pacto.invocations;
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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
	@MethodSource("args")
	void testAll(Entry<?> entry) {
		TestTarget spec = entry.handle(spec(target));
		Object value = entry.in();
		assertThat(invocations(spec).invocations()).singleElement() //
				.satisfies(i -> assertThat(i.matcher()) //
						.isInstanceOfSatisfying(entry.type(), m -> assertSoftly(s -> { //
							s.assertThat(m.value()).isEqualTo(value);
							s.assertThat(m).hasToString(entry.toStringFormat(), value);
						})));
	}

	static record Entry<T>(Class<? extends PactoMatcher<?>> type, T in, BiConsumer<TestTarget, T> consumer,
			String toStringFormat) {

		public TestTarget handle(TestTarget target) {
			consumer.accept(target, in);
			return target;
		}

	};

	private static List<Entry<?>> args() {
		UUID uuid = UUID.fromString("5d9c57fe-d2ea-42aa-b2f1-d203d6bb6cb5");
		String hex = "0000FFFF";
		String string = "xyz";
		Number number = 123;
		long longVal = 123L;
		return List.of( //
				new Entry<>(NullValueArg.class, null, (o, v) -> o.stringArg(nullValue()), "nullValue"), //
				new Entry<>(StringTypeArg.class, string, (o, v) -> o.stringArg(stringType(v)), "stringType(%s)"), //
				new Entry<>(IncludeStrArg.class, string, (o, v) -> o.stringArg(includeStr(v)), "includeStr(%s)"), //
				new Entry<>(HexValueArg.class, hex, (o, v) -> o.stringArg(hex(v)), "hex(%s)"), //
				new Entry<>(NumberTypeArg.class, number, (o, v) -> o.numberArg(numberType(v)), "numberType(%s)"), //
				new Entry<>(IdArg.class, longVal, (o, v) -> o.numberArg(id(v)), "id(%s)"), //
				new Entry<>(UuidArg.class, uuid, (o, v) -> o.uuidArg(uuid(v.toString())), "uuid(%s)"), //
				new Entry<>(UuidArg.class, uuid, (o, v) -> o.uuidArg(uuid(v)), "uuid(%s)") //
		);
	}

}
