package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.DslPartAssert.assertThatDslPart;
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.PactoDslBuilder.dslFrom;

import org.junit.jupiter.api.Test;

import com.github.pfichtner.pacto.matchers.TestTarget;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

class PactoSimpleTest {

	@Test
	void testArray() {
		TestTarget spec = spec(new TestTarget());
		Object value = new Integer[] { 1, 2, 3 };
		spec.objectArg(value);

		PactDslJsonBody expected = new PactDslJsonBody().equalTo("objectArg", value);
		assertThatDslPart(dslFrom(spec)).isEqualToDslPart(expected);
	}

}
