package com.github.pfichtner.pacto.matchers;

public class BooleanValueArg extends PactoMatcher<Boolean> {

	public BooleanValueArg(boolean value) {
		super(value);
		withToStringFormat("booleanvalue(%s)");
	}

}
