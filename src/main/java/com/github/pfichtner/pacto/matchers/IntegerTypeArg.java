package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class IntegerTypeArg extends PactoMatcher<String> {

	private final long value;

	public IntegerTypeArg(long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	@Override
	public String toString() {
		return format("integer(%d)", value);
	}

}
