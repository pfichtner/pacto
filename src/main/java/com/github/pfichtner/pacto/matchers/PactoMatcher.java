package com.github.pfichtner.pacto.matchers;

import org.mockito.ArgumentMatcher;

public class PactoMatcher<T> implements ArgumentMatcher<T> {

	private T value;

	public PactoMatcher(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	@Override
	public boolean matches(T argument) {
		throw new IllegalStateException("not implemented");
	}

}
