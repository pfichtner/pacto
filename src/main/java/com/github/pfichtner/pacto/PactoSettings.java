package com.github.pfichtner.pacto;

/**
 * Settings that control how a DTO spec is interpreted when generating Pact
 * contracts.
 *
 * <p>
 * Two matching modes are supported:
 * </p>
 *
 * <ul>
 * <li><b>Strict (default)</b> — Both the value <em>and</em> the type must
 * match. In strict mode, passing a concrete value requires the provider to
 * return exactly that value. Example: {@code setAge(42)} requires
 * {@code 42}.</li>
 * <li><b>Lenient</b> — Only the declared Java type is enforced. Matching is
 * performed against the declared parameter type of the DTO's setter method for
 * the property, or the field's declared type if no setter is available.
 * Example: {@code setAge(42)} with a {@code setAge(long)} method accepts any
 * {@code long} value.</li>
 * </ul>
 *
 * <h1>Important: Matchers vs. Concrete Values</h1>
 * <p>
 * The mode is only relevant when you pass <em>concrete values</em>. If you use
 * a matcher, it always behaves according to the matcher itself, independent of
 * the mode:
 * </p>
 *
 * <pre>
 * Strict mode:
 *   setAge(42)                 → must match exactly 42
 *   setAge(integerType(42))    → must match any integer
 *
 * Lenient mode:
 *   setAge(42)                 → must match any value of the setter’s type (e.g. any int or any long)
 *   setAge(integerType(42))    → must match any integer
 * </pre>
 *
 * <p>
 * In short: the strict/lenient setting only affects how <em>concrete
 * values</em> are treated.
 * </p>
 */
public interface PactoSettings {

	/**
	 * Switches this settings instance to lenient mode where only the DTO-declared
	 * type (the setter parameter type, or the field type if no setter exists) is
	 * enforced for concrete values.
	 *
	 * @return this settings instance in lenient mode
	 */
	PactoSettings lenient();

	/**
	 * Enables or disables lenient mode explicitly.
	 *
	 * @param lenient {@code true} to enable lenient mode (concrete values are
	 *                treated as examples and only the declared type is enforced),
	 *                {@code false} to enable strict mode (concrete values must
	 *                match exactly)
	 * @return this settings instance with the updated mode
	 */
	PactoSettings lenient(boolean lenient);

}
