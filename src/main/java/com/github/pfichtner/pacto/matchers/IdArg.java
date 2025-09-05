package com.github.pfichtner.pacto.matchers;

public class IdArg extends PactoMatcher<Long> {

	public IdArg(long in) {
		super(in);
		withToStringFormat("id(%s)");
	}

}
