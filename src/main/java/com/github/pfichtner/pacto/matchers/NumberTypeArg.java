package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class NumberTypeArg extends PactoMatcher<Number> {

	public NumberTypeArg(Number in) {
		super(in);
	}

	@Override
	public String toString() {
		return format("numberType(%s)", value());
	}

}
