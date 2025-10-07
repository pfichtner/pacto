package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

import java.util.regex.Pattern;

public class StringMatcherArg extends PactoMatcher<Pattern> {

	private String example;

	public StringMatcherArg(String regex) {
		this(Pattern.compile(regex));
	}

	public StringMatcherArg(Pattern regex) {
		this(regex, null);
		withToStringFormat("stringMatcher(%s)");
	}

	public StringMatcherArg(String regex, String example) {
		this(Pattern.compile(regex), example);
	}

	public StringMatcherArg(Pattern regex, String example) {
		super(regex);
		this.example = example;
		withToStringFormat(format("stringMatcher(%%s,%s)", example));
	}

	public String example() {
		return example;
	}

}
