package com.github.pfichtner.pacto.matchers;

import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Set;

import org.mockito.ArgumentMatcher;

public final class PactoMatchers {

	public static final String DEFAULT_STRING_VALUE = "string";
	public static final int DEFAULT_INTEGER_VALUE = 42;
	public static final float DEFAULT_DECIMAL_VALUE = 12.345F;

	private PactoMatchers() {
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

	public static long integerType(long in) {
		IntegerTypeArg matcher = new IntegerTypeArg(in);
		reportMatcher(matcher);
		return in;
	}

	public static float decimalType() {
		return decimalType(DEFAULT_DECIMAL_VALUE);
	}

	public static double decimalType(double in) {
		DecimalTypeArg matcher = new DecimalTypeArg(in);
		reportMatcher(matcher);
		return in;
	}

	public static float decimalType(float in) {
		DecimalTypeArg matcher = new DecimalTypeArg(in);
		reportMatcher(matcher);
		return in;
	}

	public static <T> T nullValue() {
		NullValueArg matcher = new NullValueArg();
		reportMatcher(matcher);
		return null;
	}

	public static <T> T[] maxArrayLike(T value, int max) {
		return eachLike(value, new EachLikeArg(value).max(max));
	}

	public static <T> T[] minArrayLike(T value, int min) {
		return eachLike(value, new EachLikeArg(value).min(min));
	}

	public static <T> T[] eachLike(T value) {
		return eachLike(value, new EachLikeArg(value));
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] eachLike(T value, EachLikeArg matcher) {
		reportMatcher(matcher);
		T[] values = (T[]) Array.newInstance(value.getClass(), 1);
		values[0] = value;
		return values;
	}

	public static class Lists {
		public static <T> List<T> maxArrayLike(T value, int max) {
			return eachLike(value, new EachLikeArg(value).max(max));
		}

		public static <T> List<T> minArrayLike(T value, int min) {
			return eachLike(value, new EachLikeArg(value).min(min));
		}

		public static <T> List<T> eachLike(T value) {
			return eachLike(value, new EachLikeArg(value));
		}

		private static <T> List<T> eachLike(T value, EachLikeArg matcher) {
			reportMatcher(matcher);
			return List.of(value);
		}
	}

	public static class Sets {
		public static <T> Set<T> maxArrayLike(T value, int max) {
			return eachLike(value, new EachLikeArg(value).max(max));
		}

		public static <T> Set<T> minArrayLike(T value, int min) {
			return eachLike(value, new EachLikeArg(value).min(min));
		}

		public static <T> Set<T> eachLike(T value) {
			return eachLike(value, new EachLikeArg(value));
		}

		private static <T> Set<T> eachLike(T value, EachLikeArg matcher) {
			reportMatcher(matcher);
			return Set.of(value);
		}
	}

	private static void reportMatcher(ArgumentMatcher<?> matcher) {
		mockingProgress().getArgumentMatcherStorage().reportMatcher(matcher);
	}

}
