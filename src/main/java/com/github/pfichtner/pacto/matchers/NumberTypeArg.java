package com.github.pfichtner.pacto.matchers;

public class NumberTypeArg extends PactoMatcher<Number> {

	public NumberTypeArg(Number in) {
		super(in);
		withToStringFormat("numberType(%s)");
	}

}
