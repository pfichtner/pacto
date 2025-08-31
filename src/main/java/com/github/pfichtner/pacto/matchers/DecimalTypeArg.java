package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class DecimalTypeArg extends PactoMatcher<String> {

	private final double value;

	public DecimalTypeArg(double in) {
		this.value = in;
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return format("decimal(%f)", value);
	}

}
