package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class RegexArg extends PactoMatcher<String> {

	private final String regex;
	private final String value;

	public RegexArg(String regex, String value) {
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
		return format("regex(%s, %s)", regex, value);
	}

}
