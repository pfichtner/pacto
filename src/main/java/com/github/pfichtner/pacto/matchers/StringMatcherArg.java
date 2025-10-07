package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class StringMatcherArg extends PactoMatcher<String> {

	private final String regex;

	public StringMatcherArg(String regex) {
		this(regex, null);
	}

	public StringMatcherArg(String regex, String value) {
		super(value);
		this.regex = regex;
	}

	public String regex() {
		return regex;
	}

	@Override
	public String toString() {
		String value = value();
		return value == null ? format("stringMatcher(%s)", regex) : format("stringMatcher(%s,%s)", regex, value);
	}

}
