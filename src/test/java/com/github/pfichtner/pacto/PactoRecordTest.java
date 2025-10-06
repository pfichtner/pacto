package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.Pacto.invocations;
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.stringType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.pfichtner.pacto.matchers.PostCleanMatcherStack;

class PactoRecordTest {

	static record MyRecord(String givenName, String lastName) {

	}

	// This class has no visible no-arg constructor (Pacto will call the only
	// existent one with null args!)
	static class MyDtoWithConstrucor {
		public MyDtoWithConstrucor(String givenName, String lastName) {
		}
	}

	@Test
	@PostCleanMatcherStack
	@Disabled
	// Would need class transformation (remove final modifier) like
	// InlineMockMaker/InlineByteBuddyMockMaker
	void canSubclassRecord() {
		MyRecord spec = spec(new MyRecord(stringType("Jon"), stringType("Doe")));
		assertThat(invocations(spec).invocations()).hasSize(2);
	}

	@Test
	@PostCleanMatcherStack
	void canCreateClassWithNoNoArgConstructor() {
		// we can't intercept the constructor but we can create at least the (sub)class
		assertThatNoException().isThrownBy(() -> spec(new MyDtoWithConstrucor(stringType("Jon"), stringType("Doe"))));
	}

	@Test
	@PostCleanMatcherStack
	@Disabled
	// we cannot intercept a constructor at the moment it is invoked for an
	// already-compiled class, unless that class itself has been instrumented before
	// it was loaded (e.g. via a Java Agent or runtime redefinition).
	void canInterceptClassWithNoNoArgConstructor() {
		MyDtoWithConstrucor spec = spec(new MyDtoWithConstrucor(stringType("Jon"), stringType("Doe")));
		assertThat(invocations(spec).invocations()).hasSize(2);
	}

}
