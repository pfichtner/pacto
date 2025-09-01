package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class IntegerTypeArg extends PactoMatcher<Long> {

	public IntegerTypeArg(long value) {
		super(value);
	}

	@Override
	public String toString() {
		return format("integer(%d)", getValue());
	}

}
