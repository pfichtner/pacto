package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class BooleanTypeArg extends PactoMatcher<Boolean> {

	private final boolean value;

	public BooleanTypeArg(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public String toString() {
		return format("booleantype(%s)", value);
	}

}
