package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

import java.util.regex.Pattern;

public class StringMatcherArg extends PactoMatcher<Pattern> {

	private String example;

	public StringMatcherArg(Pattern regex, String example) {
		super(regex);
		this.example = example;
		withToStringFormat(this.example == null ? "stringMatcher(%s)" : format("stringMatcher(%%s,%s)", example));
	}

	public String example() {
		return example;
	}

}
