package com.github.pfichtner.pacto.matchers;

public class IncludeStrArg extends PactoMatcher<String> {

	public IncludeStrArg(String value) {
		super(value);
		withToStringFormat("includeStr(%s)");
	}

}
