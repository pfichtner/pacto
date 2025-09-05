package com.github.pfichtner.pacto.matchers;

public class HexValueArg extends PactoMatcher<String> {

	public HexValueArg(String value) {
		super(value);
		withToStringFormat("hex(%s)");
	}

}
