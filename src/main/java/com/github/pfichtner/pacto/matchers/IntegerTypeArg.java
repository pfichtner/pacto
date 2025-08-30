package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class IntegerTypeArg extends PactoMatcher<String> {

	private final int value;

	public IntegerTypeArg(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return format("integer(%d)", value);
	}

}
