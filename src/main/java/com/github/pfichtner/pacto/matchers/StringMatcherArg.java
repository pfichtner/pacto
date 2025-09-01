package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class StringMatcherArg extends PactoMatcher<String> {

	private final String regex;
	private final String value;

	public StringMatcherArg(String regex, String value) {
		this.regex = regex;
		this.value = value;
	}

	public String getRegex() {
		return regex;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return format("stringMatcher(%s, %s)", regex, value);
	}

}
