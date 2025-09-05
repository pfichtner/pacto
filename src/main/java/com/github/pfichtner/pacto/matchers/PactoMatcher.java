package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class PactoMatcher<T> {

	private final T value;
	private String toString;

	public PactoMatcher(T value) {
		this.value = value;
	}

	public T value() {
		return value;
	}

	protected PactoMatcher<T> withToStringFormat(String toStringFormat) {
		toString = format(toStringFormat, value());
		return this;
	}

	@Override
	public String toString() {
		return toString == null ? super.toString() : toString;
	}

}
