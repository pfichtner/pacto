package com.github.pfichtner.pacto.matchers;

public class IntegerTypeArg extends PactoMatcher<Long> {

	public IntegerTypeArg(long value) {
		super(value);
		withToStringFormat("integer(%d)");
	}

}
