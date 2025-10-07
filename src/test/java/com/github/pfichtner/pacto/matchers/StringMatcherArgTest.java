package com.github.pfichtner.pacto.matchers;

import static com.github.pfichtner.pacto.ApprovalsHelper.scrubWithJsonPath;
import static com.github.pfichtner.pacto.ApprovalsHelper.toJson;
import static com.github.pfichtner.pacto.Pacto.recorder;
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.PactoDslBuilder.dslFrom;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.stringMatcher;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.StringMatcherMode.FIXED_EXAMPLE_VALUE;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.StringMatcherMode.RANDOM_VALUE;
import static org.approvaltests.JsonApprovals.verifyAsJson;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StringMatcherArgTest {

	TestTarget target = spec(new TestTarget());

	@Test
	void regexWithExample() {
		target.stringArg(stringMatcher("EUR|USD", "EUR"));
		assertThat(recorder(target).invocations()).singleElement()
				.satisfies(i -> assertThat(i.matcher()).hasToString("stringMatcher(EUR|USD,EUR)"));
		verifyAsJson(toJson(dslFrom(target)));
	}

	@Test
	void regexOnlyFixedValue() {
		target.stringArg(stringMatcher("EUR|USD", FIXED_EXAMPLE_VALUE));
		assertThat(recorder(target).invocations()).singleElement()
				.satisfies(i -> assertThat(i.matcher().toString()).matches("stringMatcher\\(EUR\\|USD\\,.{3}\\)"));
		verifyAsJson(scrubWithJsonPath(toJson(dslFrom(target)), "$.body.stringArg", "$$stringArg$$"));
	}

	@Test
	void regexOnlyRandomValue() {
		target.stringArg(stringMatcher("EUR|USD", RANDOM_VALUE));
		assertThat(recorder(target).invocations()).singleElement()
				.satisfies(i -> assertThat(i.matcher()).hasToString("stringMatcher(EUR|USD)"));
		verifyAsJson(scrubWithJsonPath(toJson(dslFrom(target)), "$.body.stringArg", "$$stringArg$$"));
	}

}
