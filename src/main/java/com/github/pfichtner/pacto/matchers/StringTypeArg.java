package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class StringTypeArg extends PactoMatcher<String> {

	private final String value;

	public StringTypeArg(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return format("stringtype(%s)", value);
	}

}
