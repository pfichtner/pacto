package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class HexValueArg extends PactoMatcher<String> {

	public HexValueArg(String value) {
		super(value);
	}

	@Override
	public String toString() {
		return format("hex(%s)", value());
	}

}
