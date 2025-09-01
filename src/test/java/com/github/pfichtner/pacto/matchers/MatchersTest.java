package com.github.pfichtner.pacto.matchers;

import static com.github.pfichtner.pacto.Pacto.invocations;
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.Matchers.decimalType;
import static com.github.pfichtner.pacto.matchers.Matchers.integerType;
import static com.github.pfichtner.pacto.matchers.Matchers.maxArrayLike;
import static com.github.pfichtner.pacto.matchers.Matchers.minArrayLike;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.pfichtner.pacto.matchers.Matchers.Lists;
import com.github.pfichtner.pacto.matchers.Matchers.Sets;

import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

class MatchersTest {

	@Test
	void canCompilePrimitives() {
		floatArg(decimalType());
		floatArg(decimalType(1.23F));
		floatWrapperArg(decimalType());
		floatWrapperArg(decimalType(1.23F));
		doubleArg(decimalType());
		doubleArg(decimalType(1.23));
		// TODO fix to get rid of cast
		doubleWrapperArg((double) decimalType());
		doubleWrapperArg(decimalType(1.23));

		intArg(integerType());
		intArg(integerType(42));
		integerWrapperArg(integerType());
		integerWrapperArg(integerType(42));
		longArg(integerType());
		longArg(integerType(42L));
		// TODO fix to get rid of cast
		longWrapperArg((long) integerType());
		longWrapperArg(integerType(42L));
	}

	@Test
	void canCompileCollections() {
		arrayArg(minArrayLike(new Foo(), 1));
		arrayArg(maxArrayLike(new Foo(), 2));
		listArg(Lists.minArrayLike(new Foo(), 3));
		listArg(Lists.maxArrayLike(new Foo(), 4));
		setArg(Sets.minArrayLike(new Foo(), 5));
		setArg(Sets.maxArrayLike(new Foo(), 6));
	}

	@Setter
	public static class Foo {
		Bar[] bars1;
		List<Bar> bars2;
		Set<Bar> bars3;
	}

	@Data
	@Accessors(chain = true, fluent = true)
	public static class Bar {
		private String value;
	}

	@Test
	void testMinArrayLike() {
		Foo foo = spec(new Foo());
		int min = 101;
		foo.setBars1(minArrayLike(spec(new Bar()).value("array-min"), min));
		foo.setBars2(Lists.minArrayLike(spec(new Bar()).value("list-min"), min));
		foo.setBars3(Sets.minArrayLike(spec(new Bar()).value("set-min"), min));
		assertThat(invocations(foo).getAllInvocations()).hasSize(3).allSatisfy(i -> assertThat(i.getMatcher()) //
				.isInstanceOfSatisfying(EachLikeArg.class, //
						m -> {
							assertThat(m.toString()).startsWith("minArrayLike(").contains(String.valueOf(min));
							assertThat(m.getMin()).isEqualTo(min);
						}));
	}

	@Test
	void testMaxArrayLike() {
		Foo foo = spec(new Foo());
		int max = 102;
		foo.setBars1(maxArrayLike(spec(new Bar()).value("array-max"), max));
		foo.setBars2(Lists.maxArrayLike(spec(new Bar()).value("list-max"), max));
		foo.setBars3(Sets.maxArrayLike(spec(new Bar()).value("set-max"), max));
		assertThat(invocations(foo).getAllInvocations()).hasSize(3).allSatisfy(i -> assertThat(i.getMatcher()) //
				.isInstanceOfSatisfying(EachLikeArg.class, //
						m -> {
							assertThat(m.toString()).startsWith("maxArrayLike(").contains(String.valueOf(max));
							assertThat(m.getMax()).isEqualTo(max);
						}));
	}

	void floatArg(float value) {
	}

	void floatWrapperArg(Float value) {
	}

	void doubleArg(double value) {
	}

	void doubleWrapperArg(Double value) {
	}

	void intArg(int value) {
	}

	void integerWrapperArg(Integer value) {
	}

	void longArg(long value) {
	}

	void longWrapperArg(Long value) {
	}

	private void arrayArg(Foo[] array) {
	}

	private void listArg(List<Foo> list) {
	}

	private void setArg(Set<Foo> list) {
	}

}
