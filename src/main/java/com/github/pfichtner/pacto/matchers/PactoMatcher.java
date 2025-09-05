package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class PactoMatcher<T> {

	private final T value;
	private String toStringFormat;

	public PactoMatcher(T value) {
		this.value = value;
	}

	public T value() {
		return value;
	}

	protected PactoMatcher<T> withToStringFormat(String toStringFormat) {
		this.toStringFormat = toStringFormat;
		return this;
	}

	@Override
	public String toString() {
		return format(toStringFormat, value());
	}

}
