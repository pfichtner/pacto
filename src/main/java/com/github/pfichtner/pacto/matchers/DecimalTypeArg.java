package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class DecimalTypeArg extends PactoMatcher<Double> {

	public DecimalTypeArg(double in) {
		super(in);
	}

	@Override
	public String toString() {
		return format("decimal(%f)", value());
	}

}
