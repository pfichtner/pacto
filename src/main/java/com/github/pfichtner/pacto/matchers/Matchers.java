package com.github.pfichtner.pacto.matchers;

import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

import org.mockito.ArgumentMatcher;

public final class Matchers {

	public static final String DEFAULT_STRING_VALUE = "string";
	public static final int DEFAULT_INTEGER_VALUE = 42;

	private Matchers() {
		super();
	}

	public static String stringType() {
		return stringType(DEFAULT_STRING_VALUE);
	}

	public static String stringType(String in) {
		StringTypeArg matcher = new StringTypeArg(in);
		reportMatcher(matcher);
		return in;
	}

	public static String regex(String regex, String in) {
		RegexArg matcher = new RegexArg(regex, in);
		reportMatcher(matcher);
		return in;
	}

	public static int integerType() {
		return integerType(DEFAULT_INTEGER_VALUE);
	}

	public static int integerType(int in) {
		IntegerTypeArg matcher = new IntegerTypeArg(in);
		reportMatcher(matcher);
		return in;
	}

	private static void reportMatcher(ArgumentMatcher<?> matcher) {
		mockingProgress().getArgumentMatcherStorage().reportMatcher(matcher);
	}

}
