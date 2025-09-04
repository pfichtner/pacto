package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class IncludeStrArg extends PactoMatcher<String> {

	public IncludeStrArg(String value) {
		super(value);
	}

	@Override
	public String toString() {
		return format("includeStr(%s)", value());
	}

}
