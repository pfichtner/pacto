package com.github.pfichtner.dact;

import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

import org.mockito.ArgumentMatcher;

import com.github.pfichtner.dact.matchers.IntegerTypeArg;
import com.github.pfichtner.dact.matchers.RegexArg;
import com.github.pfichtner.dact.matchers.StringTypeArg;

public class PactMatchers {

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

	public static int integerType(int in) {
		IntegerTypeArg matcher = new IntegerTypeArg(in);
		reportMatcher(matcher);
		return in;
	}

	private static void reportMatcher(ArgumentMatcher<?> matcher) {
		mockingProgress().getArgumentMatcherStorage().reportMatcher(matcher);
	}

}
