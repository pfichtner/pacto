package com.github.pfichtner.pacto.matchers;

public class BooleanTypeArg extends PactoMatcher<Boolean> {

	public BooleanTypeArg(boolean value) {
		super(value);
		withToStringFormat("booleantype(%s)");
	}

}
