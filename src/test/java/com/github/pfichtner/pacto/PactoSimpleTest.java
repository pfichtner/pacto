package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.DslPartAssert.assertThatDslPart;
import static com.github.pfichtner.pacto.Pacto.delegate;
import static com.github.pfichtner.pacto.Pacto.invocations;
import static com.github.pfichtner.pacto.Pacto.like;
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.Pacto.withSettings;
import static com.github.pfichtner.pacto.PactoDslBuilder.dslFrom;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.stringType;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.pfichtner.pacto.matchers.TestTarget;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

class PactoSimpleTest {

	@Test
	void testLenient() {
		TestTarget spec = spec(new TestTarget(), withSettings().lenient());
		spec.objectArg("123");
		DslPart expected = new PactDslJsonBody().stringType("objectArg", "123");
		assertThatDslPart(dslFrom(spec)).isEqualToDslPart(expected);
	}

	@Test
	void testStrict() {
		TestTarget spec = spec(new TestTarget(), withSettings());
		spec.objectArg("123");
		DslPart expected = new PactDslJsonBody().stringValue("objectArg", "123");
		assertThatDslPart(dslFrom(spec)).isEqualToDslPart(expected);
	}

	@Test
	void testLenientCanHoldStrict() {
		TestTarget lenientOuter = lenientOuterStrictInner();
		DslPart expected = new PactDslJsonBody().object("objectArg").stringValue("objectArg", "123").closeObject();
		assertThatDslPart(dslFrom(lenientOuter)).isEqualToDslPart(expected);
	}

	@Test
	void testStrictCanHoldLenient() {
		TestTarget strictOuter = strictOuterLenientInner();
		DslPart expected = new PactDslJsonBody().object("objectArg",
				new PactDslJsonBody().stringType("objectArg", "123"));
		assertThatDslPart(dslFrom(strictOuter)).isEqualToDslPart(expected);
	}

	@Test
	void testLikeIsEqualToStrictCanHoldLenient() {
		TestTarget outer = spec(new TestTarget(), strict());
		TestTarget inner = like(new TestTarget());
		inner.objectArg("123");
		outer.objectArg(inner);
		assertThatDslPart(dslFrom(outer)).isEqualToDslPart(dslFrom(strictOuterLenientInner()));
	}

	@Test
	void likeDoesUnpack() {
		TestTarget testTarget = new TestTarget();
		TestTarget like1 = like(testTarget);
		TestTarget like2 = like(spec(testTarget));
		assertThat(delegate(like1)).isSameAs(delegate(like2));
	}

	@Test
	void testStrictIsDefault() {
		assertThatDslPart(dslFrom(spec(new TestTarget()))) //
				.isEqualToDslPart(dslFrom(spec(new TestTarget(), withSettings()))) //
				.isEqualToDslPart(dslFrom(spec(new TestTarget(), strict())));
	}

	private TestTarget lenientOuterStrictInner() {
		TestTarget outer = spec(new TestTarget(), lenient());
		TestTarget inner = spec(new TestTarget(), strict());
		inner.objectArg("123");
		outer.objectArg(inner);
		return outer;
	}

	private TestTarget strictOuterLenientInner() {
		TestTarget outer = spec(new TestTarget(), strict());
		TestTarget inner = spec(new TestTarget(), lenient());
		inner.objectArg("123");
		outer.objectArg(inner);
		return outer;
	}

	@Test
	void testArray() {
		TestTarget spec = spec(new TestTarget());
		Object value = new Integer[] { 1, 2, 3 };
		spec.objectArg(value);

		DslPart expected = new PactDslJsonBody().equalTo("objectArg", value);
		assertThatDslPart(dslFrom(spec)).isEqualToDslPart(expected);
	}

	@SuppressWarnings("static-access")
	@Test
	void doesCopyConstantFields() throws Exception {
		TestTarget spec = spec(new TestTarget());
		assertThat(spec.someStaticData4).isEqualTo(List.of("1", "2", "3", "4", "5"));
		assertThat(access(spec, "someStaticData4")).isEqualTo(List.of("1", "2", "3", "4", "5"));
		assertThat(access(spec, "someStaticData3")).isEqualTo(List.of("1", "2", "3"));
		assertThat(access(spec, "someStaticData2")).isEqualTo(List.of("1"));
		assertThat(access(spec, "someStaticData1")).isEqualTo(List.of());

		assertThat(spec.someData4).isEqualTo(List.of("A", "B", "C", "D", "E"));
		assertThat(access(spec, "someData4")).isEqualTo(List.of("A", "B", "C", "D", "E"));
		assertThat(access(spec, "someData3")).isEqualTo(List.of("A", "B", "C"));
		assertThat(access(spec, "someData2")).isEqualTo(List.of("A"));
		assertThat(access(spec, "someData1")).isEqualTo(List.of());
	}

	@Test
	void testSpecOfSpec() {
		TestTarget specOrigin = spec(new TestTarget());
		specOrigin.objectArg(stringType("a string is an object"));
		TestTarget specCopy = spec(specOrigin);
		assertThat(specOrigin).usingRecursiveComparison().isEqualTo(specCopy);

		specCopy.stringArg("another string");
		assertThat(invocations(specOrigin).invocations()).hasSize(1);
		assertThat(invocations(specCopy).invocations()).hasSize(2);
	}

	private Object access(TestTarget source, String fieldname) throws Exception {
		Field field = TestTarget.class.getDeclaredField(fieldname);
		field.setAccessible(true);
		return field.get(source);
	}

	private static PactoSettings lenient() {
		return lenient(true);
	}

	private static PactoSettings strict() {
		return lenient(false);
	}

	private static PactoSettings lenient(boolean lenient) {
		return withSettings().lenient(lenient);
	}

}
