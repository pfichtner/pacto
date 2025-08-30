package com.github.pfichtner.pacto.matchers;

import org.mockito.ArgumentMatcher;

public class IntegerTypeArg implements ArgumentMatcher<String> {

	private final int value;

	public IntegerTypeArg(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public boolean matches(String argument) {
		return argument != null;
	}

	@Override
	public String toString() {
		return "integer(" + value + ")";
	}

}
