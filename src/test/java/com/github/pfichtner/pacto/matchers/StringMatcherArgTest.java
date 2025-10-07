package com.github.pfichtner.pacto.matchers;

import static com.github.pfichtner.pacto.ApprovalsHelper.scrubBodyStringArg;
import static com.github.pfichtner.pacto.ApprovalsHelper.toJson;
import static com.github.pfichtner.pacto.Pacto.recorder;
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.PactoDslBuilder.dslFrom;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.stringMatcher;
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
	void regexOnly() {
		target.stringArg(stringMatcher("EUR|USD"));
		assertThat(recorder(target).invocations()).singleElement()
				.satisfies(i -> assertThat(i.matcher()).hasToString("stringMatcher(EUR|USD)"));
		verifyAsJson(scrubBodyStringArg(toJson(dslFrom(target))));
	}

}
