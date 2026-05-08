package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.Pacto.delegate;
import static com.github.pfichtner.pacto.Pacto.invocations;
import static com.github.pfichtner.pacto.Pacto.isSpec;
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.stringType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.pfichtner.pacto.matchers.PostCleanMatcherStack;
import com.github.pfichtner.pacto.matchers.TestTarget;

class PactoRecordTest {

	// Force-load Pacto before any record class is loaded, so the AgentBuilder
	// transformer can remove the final modifier from records at load time.
	static {
		Pacto.withSettings();
	}

	static record MyRecord(String givenName, String lastName) {

	}

	static record SimpleRecord(String value) {

	}

	static record MultiTypeRecord(String name, int age, double height, boolean active) {

	}

	@Test
	@PostCleanMatcherStack
	void canSubclassRecord() {
		MyRecord spec = spec(new MyRecord(stringType("Jon"), stringType("Doe")));
		assertThat(spec).isNotNull();
		assertThat(invocations(spec).invocations()).isEmpty();
	}

	@Test
	@PostCleanMatcherStack
	void isSpecReturnsTrueForRecordProxy() {
		MyRecord spec = spec(new MyRecord("Jon", "Doe"));
		assertThat(isSpec(spec)).isTrue();
	}

	@Test
	@PostCleanMatcherStack
	void isSpecReturnsFalseForPlainRecord() {
		assertThat(isSpec(new MyRecord("Jon", "Doe"))).isFalse();
	}

	@Test
	@PostCleanMatcherStack
	void delegateReturnsOriginalRecord() {
		MyRecord original = new MyRecord("Jon", "Doe");
		MyRecord spec = spec(original);
		assertThat(delegate(spec)).isSameAs(original);
	}

	@Test
	@PostCleanMatcherStack
	void recordAccessorMethodsWork() {
		MyRecord original = new MyRecord("Jon", "Doe");
		MyRecord spec = spec(original);
		assertThat(spec.givenName()).isEqualTo("Jon");
		assertThat(spec.lastName()).isEqualTo("Doe");
	}

	@Test
	@PostCleanMatcherStack
	void specOfSpecReturnsSameInstance() {
		MyRecord original = new MyRecord("Jon", "Doe");
		MyRecord first = spec(original);
		MyRecord second = spec(first);
		assertThat(isSpec(second)).isTrue();
		assertThat(second).isSameAs(first);
		assertThat(second.givenName()).isEqualTo("Jon");
	}

	@Test
	@PostCleanMatcherStack
	void recordWithMultipleTypes() {
		MultiTypeRecord record = new MultiTypeRecord("Alice", 30, 1.75, true);
		MultiTypeRecord spec = spec(record);
		assertThat(spec.name()).isEqualTo("Alice");
		assertThat(spec.age()).isEqualTo(30);
		assertThat(spec.height()).isEqualTo(1.75);
		assertThat(spec.active()).isTrue();
	}

	@Test
	@PostCleanMatcherStack
	void recordAsNestedObjectInDto() {
		TestTarget outer = spec(new TestTarget());
		TestTarget inner = spec(new TestTarget());
		outer.objectArg(inner);
		assertThat(invocations(outer).invocations()).hasSize(1);
	}

	// This class has no visible no-arg constructor (Pacto will call the only
	// existent one with null args!)
	static class MyDtoWithConstrucor {
		public MyDtoWithConstrucor(String givenName, String lastName) {
		}
	}

	@Test
	@PostCleanMatcherStack
	void canCreateClassWithNoNoArgConstructor() {
		assertThatNoException().isThrownBy(() -> spec(new MyDtoWithConstrucor(stringType("Jon"), stringType("Doe"))));
	}

	@Test
	@PostCleanMatcherStack
	@Disabled
	void canInterceptClassWithNoNoArgConstructor() {
		MyDtoWithConstrucor spec = spec(new MyDtoWithConstrucor(stringType("Jon"), stringType("Doe")));
		assertThat(invocations(spec).invocations()).hasSize(2);
	}

}
