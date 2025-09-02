package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class HexArg extends PactoMatcher<String> {

	public HexArg(String value) {
		super(value);
	}

	@Override
	public String toString() {
		return format("hex(%s)", value());
	}

}
