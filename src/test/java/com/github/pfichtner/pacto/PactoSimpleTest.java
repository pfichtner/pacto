package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.DslPartAssert.assertThatDslPart;
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.Pacto.withSettings;
import static com.github.pfichtner.pacto.PactoDslBuilder.dslFrom;
import static org.junit.jupiter.api.Assertions.fail;

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
		TestTarget outer = spec(new TestTarget(), withSettings().lenient(true));
		TestTarget inner = spec(new TestTarget(), withSettings().lenient(false));
		inner.objectArg("123");
		outer.objectArg(inner);
		DslPart expected = new PactDslJsonBody().object("objectArg").stringValue("objectArg", "123").closeObject();
		assertThatDslPart(dslFrom(outer)).isEqualToDslPart(expected);
	}

	@Test
	void testStrictCanHoldLenient() {
		TestTarget outer = spec(new TestTarget(), withSettings().lenient(false));
		TestTarget inner = spec(new TestTarget(), withSettings().lenient(true));
		inner.objectArg("123");
		outer.objectArg(inner);
		DslPart expected = new PactDslJsonBody().object("objectArg",
				new PactDslJsonBody().stringType("objectArg", "123"));
		assertThatDslPart(dslFrom(outer)).isEqualToDslPart(expected);
	}

	@Test
	void testStrictIsDefault() {
		TestTarget spec = spec(new TestTarget());
		assertThatDslPart(dslFrom(spec)).isEqualToDslPart(dslFrom(spec(new TestTarget(), withSettings())));
	}

	@Test
	void testArray() {
		TestTarget spec = spec(new TestTarget());
		Object value = new Integer[] { 1, 2, 3 };
		spec.objectArg(value);

		DslPart expected = new PactDslJsonBody().equalTo("objectArg", value);
		assertThatDslPart(dslFrom(spec)).isEqualToDslPart(expected);
	}

}
