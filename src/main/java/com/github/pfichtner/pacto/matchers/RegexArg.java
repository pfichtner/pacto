package com.github.pfichtner.pacto.matchers;

import org.mockito.ArgumentMatcher;

public class RegexArg implements ArgumentMatcher<String> {

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
	public boolean matches(String argument) {
		return argument != null && argument.matches(regex);
	}

	@Override
	public String toString() {
		return "regex(" + regex + ", " + value + ")";
	}

}
