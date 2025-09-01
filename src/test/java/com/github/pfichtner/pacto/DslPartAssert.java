package com.github.pfichtner.pacto;

import org.assertj.core.api.AbstractAssert;
import au.com.dius.pact.consumer.dsl.DslPart; // adjust import if needed

public class DslPartAssert extends AbstractAssert<DslPartAssert, DslPart> {

	public DslPartAssert(DslPart actual) {
		super(actual, DslPartAssert.class);
	}

	public static DslPartAssert assertThatDslPart(DslPart actual) {
		return new DslPartAssert(actual);
	}

	public DslPartAssert isEqualToDslPart(DslPart expected) {
		isNotNull();

		if (!actual.getBody().equals(expected.getBody())) {
			failWithMessage("Expected body <%s> but was <%s>", expected.getBody(), actual.getBody());
		}

		if (!actual.getMatchers().equals(expected.getMatchers())) {
			failWithMessage("Expected matchers <%s> but was <%s>", expected.getMatchers(), actual.getMatchers());
		}

		return this;
	}

}
