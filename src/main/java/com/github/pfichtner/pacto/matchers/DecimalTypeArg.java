package com.github.pfichtner.pacto.matchers;

public class DecimalTypeArg extends PactoMatcher<Double> {

	public DecimalTypeArg(double in) {
		super(in);
		withToStringFormat("decimal(%f)");
	}

}
