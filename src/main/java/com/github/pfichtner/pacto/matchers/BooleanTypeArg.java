package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class BooleanTypeArg extends PactoMatcher<Boolean> {

	public BooleanTypeArg(boolean value) {
		super(value);
	}

	@Override
	public String toString() {
		return format("booleantype(%s)", getValue());
	}

}
