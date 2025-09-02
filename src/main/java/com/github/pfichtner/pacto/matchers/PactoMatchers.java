package com.github.pfichtner.pacto.matchers;

import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Set;

import org.mockito.ArgumentMatcher;

public final class PactoMatchers {

	public static final boolean DEFAULT_BOOLEAN_VALUE = true;
	public static final String DEFAULT_STRING_VALUE = "string";
	public static final int DEFAULT_INTEGER_VALUE = 42;
	public static final float DEFAULT_DECIMAL_VALUE = 12.345F;
	public static final String DEFAULT_HEX_VALUE = "1234a";

	private PactoMatchers() {
		super();
	}

	public static boolean booleanType() {
		return booleanType(DEFAULT_BOOLEAN_VALUE);
	}

	public static boolean booleanType(boolean in) {
		reportMatcher(new BooleanTypeArg(in));
		return in;
	}

	public static boolean booleanValue(boolean in) {
		reportMatcher(new BooleanValueArg(in));
		return in;
	}

	public static String stringType() {
		return stringType(DEFAULT_STRING_VALUE);
	}

	public static String stringType(String in) {
		reportMatcher(new StringTypeArg(in));
		return in;
	}

	public static String stringMatcher(String regex, String in) {
		reportMatcher(new StringMatcherArg(regex, in));
		return in;
	}

	public static int integerType() {
		return integerType(DEFAULT_INTEGER_VALUE);
	}

	public static int integerType(int in) {
		reportMatcher(new IntegerTypeArg(in));
		return in;
	}

	public static long integerType(long in) {
		reportMatcher(new IntegerTypeArg(in));
		return in;
	}

	public static float decimalType() {
		return decimalType(DEFAULT_DECIMAL_VALUE);
	}

	public static double decimalType(double in) {
		reportMatcher(new DecimalTypeArg(in));
		return in;
	}

	public static float decimalType(float in) {
		reportMatcher(new DecimalTypeArg(in));
		return in;
	}

	public static <T extends Number> T numberType(T in) {
		reportMatcher(new NumberTypeArg(in));
		return in;
	}

	public static String hex() {
		return hex(DEFAULT_HEX_VALUE);
	}

	public static String hex(String in) {
		reportMatcher(new HexArg(in));
		return in;
	}

	public static <T> T nullValue() {
		reportMatcher(new NullValueArg());
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
