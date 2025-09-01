package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class BooleanValueArg extends PactoMatcher<Boolean> {

	public BooleanValueArg(boolean value) {
		super(value);
	}

	@Override
	public String toString() {
		return format("booleanvalue(%s)", getValue());
	}

}
