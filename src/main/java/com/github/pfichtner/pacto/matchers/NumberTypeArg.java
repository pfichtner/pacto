package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class NumberTypeArg extends PactoMatcher<String> {

	private final Number value;

	public NumberTypeArg(Number in) {
		this.value = in;
	}

	public Number getValue() {
		return value;
	}

	@Override
	public String toString() {
		return format("numberType(%s)", value);
	}

}
