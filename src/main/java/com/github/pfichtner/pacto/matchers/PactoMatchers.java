package com.github.pfichtner.pacto.matchers;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.github.pfichtner.pacto.MatcherRegistry;

/**
 * Provides static methods for defining Pact matchers from DTOs.
 * <p>
 * These methods have the same names and behavior as the Pact JVM consumer
 * matchers:
 * <ul>
 * <li>stringType</li>
 * <li>integerType</li>
 * <li>decimalType</li>
 * <li>booleanType</li>
 * <li>uuid</li>
 * <li>nullValue</li>
 * <li>eachLike / minArrayLike / maxArrayLike</li>
 * </ul>
 * <p>
 * PactoMatchers is essentially syntax sugar: it captures the arguments passed
 * to the matcher methods and registers them internally for automatic Pact
 * contract generation.
 */
public final class PactoMatchers {

	public static final boolean DEFAULT_BOOLEAN_VALUE = true;
	public static final String DEFAULT_STRING_VALUE = "string";
	public static final int DEFAULT_INTEGER_VALUE = 100;
	public static final float DEFAULT_DECIMAL_VALUE = 100.0F;
	public static final String DEFAULT_HEX_VALUE = "1234a";
	public static final UUID DEFAULT_UUID_VALUE = UUID.fromString("e2490de5-5bd3-43d5-b7c4-526e33f71304");
	public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	// Prevent instantiation
	private PactoMatchers() {
		super();
	}

	/**
	 * Matches any boolean value. Delegates to Pact JVM's booleanType matcher.
	 *
	 * @return default boolean value
	 */
	public static boolean booleanType() {
		return booleanType(DEFAULT_BOOLEAN_VALUE);
	}

	/**
	 * Matches any boolean value.
	 *
	 * @param in the example boolean value
	 * @return the same value
	 */
	public static boolean booleanType(boolean in) {
		reportMatcher(new BooleanTypeArg(in));
		return in;
	}

	/**
	 * Matches a specific boolean value.
	 *
	 * @param in the value to match exactly
	 * @return the same value
	 */
	public static boolean booleanValue(boolean in) {
		reportMatcher(new BooleanValueArg(in));
		return in;
	}

	/**
	 * Matches any string value.
	 *
	 * @return default string
	 */
	public static String stringType() {
		return stringType(DEFAULT_STRING_VALUE);
	}

	/**
	 * Matches a string of the same type as the given example.
	 *
	 * @param in example string
	 * @return same string
	 */
	public static String stringType(String in) {
		reportMatcher(new StringTypeArg(in));
		return in;
	}

	/**
	 * Matches strings according to a regular expression.
	 *
	 * @param regex regex pattern
	 * @param in    example string
	 * @return same string
	 */
	public static String stringMatcher(String regex, String in) {
		reportMatcher(new StringMatcherArg(regex, in));
		return in;
	}

	/**
	 * Matches strings that include the given substring.
	 *
	 * @param in substring to include
	 * @return same string
	 */
	public static String includeStr(String in) {
		reportMatcher(new IncludeStrArg(in));
		return in;
	}

	/**
	 * Matches any integer.
	 *
	 * @return default integer
	 */
	public static int integerType() {
		return integerType(DEFAULT_INTEGER_VALUE);
	}

	/**
	 * Matches the given integer value type.
	 *
	 * @param in example int
	 * @return same value
	 */
	public static int integerType(int in) {
		reportMatcher(new IntegerTypeArg(in));
		return in;
	}

	/**
	 * Matches the given long value type.
	 *
	 * @param in example long
	 * @return same value
	 */
	public static long integerType(long in) {
		reportMatcher(new IntegerTypeArg(in));
		return in;
	}

	/**
	 * Matches any floating-point number (float or double).
	 *
	 * @return default float value
	 */
	public static float decimalType() {
		return decimalType(DEFAULT_DECIMAL_VALUE);
	}

	/**
	 * Matches a given double value type.
	 *
	 * @param in example double
	 * @return same value
	 */
	public static double decimalType(double in) {
		reportMatcher(new DecimalTypeArg(in));
		return in;
	}

	/**
	 * Matches a given float value type.
	 *
	 * @param in example float
	 * @return same value
	 */
	public static float decimalType(float in) {
		reportMatcher(new DecimalTypeArg(in));
		return in;
	}

	/**
	 * Matches any number type.
	 *
	 * @param <T> number type
	 * @param in  example value
	 * @return same value
	 */
	public static <T extends Number> T numberType(T in) {
		reportMatcher(new NumberTypeArg(in));
		return in;
	}

	/**
	 * Matches any id type.
	 *
	 * @param in example value
	 * @return same value
	 */
	public static int id(int in) {
		reportMatcher(new IdArg(in));
		return in;
	}

	/**
	 * Matches any id type.
	 *
	 * @param in example value
	 * @return same value
	 */
	public static long id(long in) {
		reportMatcher(new IdArg(in));
		return in;
	}

	/**
	 * Matches any hex string.
	 *
	 * @return default hex value
	 */
	public static String hex() {
		return hex(DEFAULT_HEX_VALUE);
	}

	/**
	 * Matches the given hex string.
	 *
	 * @param in example hex string
	 * @return same value
	 */
	public static String hex(String in) {
		reportMatcher(new HexValueArg(in));
		return in;
	}

	/**
	 * Matches any ip address string.
	 * 
	 * @param in example ip address string
	 *
	 * @return same value
	 */
	public static String ipAddress(String in) {
		reportMatcher(new IpAddressArg());
		return in;
	}

	/**
	 * Matches any UUID.
	 *
	 * @return default UUID
	 */
	public static UUID uuid() {
		return uuid(DEFAULT_UUID_VALUE);
	}

	/**
	 * Matches any UUID.
	 *
	 * @param in the example UUID
	 * @return UUID from string
	 */
	public static UUID uuid(String in) {
		return uuid(UUID.fromString(in));
	}

	/**
	 * Matches any UUID.
	 *
	 * @param in the example UUID
	 * @return the same value
	 */
	public static UUID uuid(UUID in) {
		reportMatcher(new UuidArg(in));
		return in;
	}

	/**
	 * Matches a URL composed of the given base path and optional path fragments.
	 *
	 * @param basePath      the base path of the URL
	 * @param pathFragments optional additional path fragments to append
	 * @return the base path
	 */
	public static String matchUrl(String basePath, String... pathFragments) {
		reportMatcher(new MatchUrlArg(basePath, pathFragments));
		return basePath;
	}

	/**
	 * Matches any time matching default format {@value #DEFAULT_TIME_FORMAT}.
	 *
	 * @param in the example time
	 * @return the same value
	 */
	public static LocalDateTime time(LocalDateTime in) {
		reportMatcher(new TimeArg(DEFAULT_TIME_FORMAT, toDate(in)));
		return in;
	}

	/**
	 * Matches any time matching the passed format.
	 *
	 * @param format the time format
	 * @param in     the example time
	 * @return the same value
	 */
	public static LocalDateTime time(String format, LocalDateTime in) {
		reportMatcher(new TimeArg(format, toDate(in)));
		return in;
	}

	/**
	 * Matches any time matching the default format {@value #DEFAULT_TIME_FORMAT}.
	 *
	 * @param in the example time
	 * @return the same value
	 */
	public static Date time(Date in) {
		reportMatcher(new TimeArg(DEFAULT_TIME_FORMAT, in));
		return in;
	}

	/**
	 * Matches any time matching the passed format.
	 *
	 * @param format the time format
	 * @param in     the example time
	 * @return the same value
	 */
	public static Date time(String format, Date in) {
		reportMatcher(new TimeArg(format, in));
		return in;
	}

	/**
	 * Matches any date matching default format {@value #DEFAULT_DATE_FORMAT}.
	 *
	 * @param in the example date
	 * @return the same value
	 */
	public static LocalDate date(LocalDate in) {
		reportMatcher(new DateArg(DEFAULT_DATE_FORMAT, toDate(in)));
		return in;
	}

	/**
	 * Matches any date matching the passed format.
	 *
	 * @param format the date format
	 * @param in     the example date
	 * @return the same value
	 */
	public static LocalDate date(String format, LocalDate in) {
		reportMatcher(new DateArg(format, toDate(in)));
		return in;
	}

	/**
	 * Matches any date matching the default format {@value #DEFAULT_DATE_FORMAT}.
	 *
	 * @param in the example date
	 * @return the same value
	 */
	public static Date date(Date in) {
		reportMatcher(new DateArg(DEFAULT_DATE_FORMAT, in));
		return in;
	}

	/**
	 * Matches any date matching the passed format.
	 *
	 * @param format the date format
	 * @param in     the example date
	 * @return the same value
	 */
	public static Date date(String format, Date in) {
		reportMatcher(new DateArg(format, in));
		return in;
	}

	/**
	 * Matches any date matching default format {@value #DEFAULT_DATETIME_FORMAT}.
	 *
	 * @param in the example date
	 * @return the same value
	 */
	public static LocalDate datetime(LocalDate in) {
		reportMatcher(new DatetimeArg(DEFAULT_DATETIME_FORMAT, toDate(in)));
		return in;
	}

	/**
	 * Matches any date matching the passed format.
	 *
	 * @param format the date format
	 * @param in     the example date
	 * @return the same value
	 */
	public static LocalDate datetime(String format, LocalDate in) {
		reportMatcher(new DatetimeArg(format, toDate(in)));
		return in;
	}

	/**
	 * Matches any date matching the default format
	 * {@value #DEFAULT_DATETIME_FORMAT}.
	 *
	 * @param in the example date
	 * @return the same value
	 */
	public static Date datetime(Date in) {
		reportMatcher(new DatetimeArg(DEFAULT_DATETIME_FORMAT, in));
		return in;
	}

	/**
	 * Matches any date matching the passed format.
	 *
	 * @param format the date format
	 * @param in     the example date
	 * @return the same value
	 */
	public static Date datetime(String format, Date in) {
		reportMatcher(new DatetimeArg(format, in));
		return in;
	}

	/**
	 * Matches a null value.
	 *
	 * @param <T> type
	 * @return null
	 */
	public static <T> T nullValue() {
		reportMatcher(new NullValueArg());
		return null;
	}

	/**
	 * Matches an array with at least one element like the given value.
	 *
	 * @param <T>   element type
	 * @param value example value
	 * @return array containing the value
	 */
	public static <T> T[] eachLike(T value) {
		return eachLike(value, new EachLikeArg(value));
	}

	/**
	 * Matches an array with at least {@code min} elements like the given value.
	 *
	 * @param <T>   element type
	 * @param value example value for each element
	 * @param min   minimum number of elements in the array
	 * @return an array containing at least {@code min} elements like {@code value}
	 */
	public static <T> T[] minArrayLike(T value, int min) {
		return eachLike(value, new EachLikeArg(value).min(min));
	}

	/**
	 * Matches an array with at most {@code max} elements like the given value.
	 *
	 * @param <T>   element type
	 * @param value example value for each element
	 * @param max   maximum number of elements in the array
	 * @return an array containing at most {@code max} elements like {@code value}
	 */
	public static <T> T[] maxArrayLike(T value, int max) {
		return eachLike(value, new EachLikeArg(value).max(max));
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] eachLike(T value, EachLikeArg matcher) {
		reportMatcher(matcher);
		T[] values = (T[]) Array.newInstance(value.getClass(), 1);
		values[0] = value;
		return values;
	}

	/** List variants of array matchers. */
	public static class Lists {

		/**
		 * Matches a list with at least one element like the given value.
		 *
		 * @param <T>   element type
		 * @param value example value
		 * @return list containing the example value
		 */
		public static <T> List<T> eachLike(T value) {
			return eachLike(value, new EachLikeArg(value));
		}

		/**
		 * Matches a list with at least {@code min} elements like the given value.
		 *
		 * @param <T>   element type
		 * @param value example value
		 * @param min   minimum number of elements in the list
		 * @return list containing at least {@code min} elements like {@code value}
		 */
		public static <T> List<T> minArrayLike(T value, int min) {
			return eachLike(value, new EachLikeArg(value).min(min));
		}

		/**
		 * Matches a list with at most {@code max} elements like the given value.
		 *
		 * @param <T>   element type
		 * @param value example value
		 * @param max   maximum number of elements in the list
		 * @return list containing at most {@code max} elements like {@code value}
		 */
		public static <T> List<T> maxArrayLike(T value, int max) {
			return eachLike(value, new EachLikeArg(value).max(max));
		}

		private static <T> List<T> eachLike(T value, EachLikeArg matcher) {
			reportMatcher(matcher);
			return List.of(value);
		}

	}

	/** Set variants of array matchers. */
	public static class Sets {

		/**
		 * Matches a set with at least one element like the given value.
		 *
		 * @param <T>   element type
		 * @param value example value
		 * @return set containing the example value
		 */
		public static <T> Set<T> eachLike(T value) {
			return eachLike(value, new EachLikeArg(value));
		}

		/**
		 * Matches a set with at least {@code min} elements like the given value.
		 *
		 * @param <T>   element type
		 * @param value example value
		 * @param min   minimum number of elements in the set
		 * @return set containing at least {@code min} elements like {@code value}
		 */
		public static <T> Set<T> minArrayLike(T value, int min) {
			return eachLike(value, new EachLikeArg(value).min(min));
		}

		/**
		 * Matches a set with at most {@code max} elements like the given value.
		 *
		 * @param <T>   element type
		 * @param value example value
		 * @param max   maximum number of elements in the set
		 * @return set containing at most {@code max} elements like {@code value}
		 */
		public static <T> Set<T> maxArrayLike(T value, int max) {
			return eachLike(value, new EachLikeArg(value).max(max));
		}

		private static <T> Set<T> eachLike(T value, EachLikeArg matcher) {
			reportMatcher(matcher);
			return Set.of(value);
		}

	}

	private static Date toDate(LocalDate dateToConvert) {
		return Date.from(dateToConvert.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	private static Date toDate(LocalDateTime dateToConvert) {
		return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
	}

	/** Register a matcher in the internal registry. */
	private static void reportMatcher(PactoMatcher<?> matcher) {
		MatcherRegistry.register(matcher);
	}

}
