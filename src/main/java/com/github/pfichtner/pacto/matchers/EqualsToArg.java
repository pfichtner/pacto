package com.github.pfichtner.pacto.matchers;

public class EqualsToArg extends PactoMatcher<Object> {

	public EqualsToArg(Object value) {
		super(value);
		withToStringFormat("equalsTo(%s)");
	}

}
