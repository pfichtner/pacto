package com.github.pfichtner.pacto.matchers;

public class StringTypeArg extends PactoMatcher<String> {

	public StringTypeArg(String value) {
		super(value);
		withToStringFormat("stringtype(%s)");
	}

}
