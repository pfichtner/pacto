package com.github.pfichtner.pacto.matchers;

public class PactoMatcher<T> {

	private final T value;

	public PactoMatcher(T value) {
		this.value = value;
	}

	public T value() {
		return value;
	}

}
