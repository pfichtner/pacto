package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class StringTypeArg extends PactoMatcher<String> {

	public StringTypeArg(String value) {
		super(value);
	}

	@Override
	public String toString() {
		return format("stringtype(%s)", value());
	}

}
