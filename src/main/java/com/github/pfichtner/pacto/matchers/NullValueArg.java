package com.github.pfichtner.pacto.matchers;

public class NullValueArg extends PactoMatcher<Object> {

	public NullValueArg() {
		super(null);
		withToStringFormat("nullValue");
	}

}
