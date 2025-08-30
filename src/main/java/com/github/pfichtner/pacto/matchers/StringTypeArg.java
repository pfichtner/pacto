package com.github.pfichtner.pacto.matchers;

import org.mockito.ArgumentMatcher;

public class StringTypeArg implements ArgumentMatcher<String> {

	private final String value;

	public StringTypeArg(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean matches(String argument) {
		return argument != null;
	}

	@Override
	public String toString() {
		return "stringtype(" + value + ")";
	}

}
