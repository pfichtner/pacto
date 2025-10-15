package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.RecordingAdvice.FIELDNAME_DELEGATE;
import static com.github.pfichtner.pacto.RecordingAdvice.FIELDNAME_RECORDER;
import static com.github.pfichtner.pacto.util.Reflections.copyFields;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static net.bytebuddy.description.modifier.FieldManifestation.FINAL;
import static net.bytebuddy.description.modifier.FieldPersistence.TRANSIENT;
import static net.bytebuddy.description.modifier.SyntheticState.SYNTHETIC;
import static net.bytebuddy.description.modifier.Visibility.PRIVATE;
import static net.bytebuddy.description.modifier.Visibility.PUBLIC;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.isFinal;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType.NamingStrategy;

/**
 * Pacto is the core class of the pacto library. It provides proxy-based
 * recording of DTO method calls to generate Pact matchers automatically.
 * <p>
 * You can wrap your DTO with {@link #spec(Object)} to record interactions and
 * then generate a Pact contract. Recorded invocations can be inspected with
 * {@link #invocations(Object)}. You can retrieve the original object behind a
 * proxy with {@link #delegate(Object)}.
 */
public class Pacto {

	protected static interface HasDelegate<T> {
		T __pacto_delegate();
	}

	protected static interface HasRecorder {
		Recorder __pacto_recorder();
	}

	private static final String getDelegateMethodname = getOnlyMethod(HasDelegate.class).getName();
	private static final String getRecorderMethodname = getOnlyMethod(HasRecorder.class).getName();

	private static Method getOnlyMethod(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		if (methods.length != 1) {
			throw new IllegalStateException(
					format("%s defines not exactly one method, found %s", clazz.getName(), Arrays.toString(methods)));
		}
		return methods[0];
	}

	public static PactoSettings withSettings() {
		return new PactoSettingsImpl();
	}

	/**
	 * Wraps a DTO with a proxy that records method invocations and matcher
	 * arguments. If the argument is already wrapped a copy is returned.  
	 *
	 * @param intercept the original DTO to wrap
	 * @param <T>       the type of the DTO
	 * @return a proxy instance of the DTO that records matcher calls
	 * @throws RuntimeException if proxy creation fails
	 */
	public static <T> T spec(T intercept) {
		return spec(intercept, withSettings());

	}

	/**
	 * Wraps a DTO with a proxy that records method invocations and matcher
	 * arguments.
	 *
	 * @param intercept the original DTO to wrap
	 * @param <T>       the type of the DTO
	 * @return a proxy instance of the DTO that records matcher calls
	 * @throws RuntimeException if proxy creation fails
	 */
	public static <T> T spec(T intercept, PactoSettings settings) {
		return isSpec(intercept) //
				? newInstance(delegate(intercept), recorder(intercept).copy()) //
				: newInstance(intercept, new Recorder(settings));
	}

	private static <T> T newInstance(T intercept, Recorder recorder) {
		@SuppressWarnings("unchecked")
		Class<T> type = (Class<T>) intercept.getClass();
		try {
			return copyFields(intercept, constructor(type, recorder.getClass()).newInstance(intercept, recorder));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> Constructor<? extends T> constructor(Class<T> type, Class<?> clazz)
			throws NoSuchMethodException {
		return proxyClass(type).getDeclaredConstructor(type, clazz);
	}

	private static <T> Class<? extends T> proxyClass(Class<T> type) throws NoSuchMethodException, SecurityException {
		Constructor<?> superConstructor = getConstructor(type);
		return new ByteBuddy() //
				.with(new NamingStrategy.SuffixingRandom("PactoProxy")) //
				.subclass(type) //
				.implement(HasDelegate.class, HasRecorder.class) //
				.defineField(FIELDNAME_DELEGATE, type, PRIVATE, FINAL, TRANSIENT) //
				.defineField(FIELDNAME_RECORDER, Recorder.class, PRIVATE, FINAL, TRANSIENT) //
				.defineConstructor(PUBLIC).withParameters(type, Recorder.class).intercept( //
						MethodCall.invoke(superConstructor) //
								.with((Object[]) new Object[superConstructor.getParameterCount()]) //
								.andThen(FieldAccessor.ofField(FIELDNAME_DELEGATE).setsArgumentAt(0)) //
								.andThen(FieldAccessor.ofField(FIELDNAME_RECORDER).setsArgumentAt(1)) //
				) //
				.defineMethod(getDelegateMethodname, type, PUBLIC, SYNTHETIC) //
				.intercept(FieldAccessor.ofField(FIELDNAME_DELEGATE)) //
				.defineMethod(getRecorderMethodname, Recorder.class, PUBLIC, SYNTHETIC) //
				.intercept(FieldAccessor.ofField(FIELDNAME_RECORDER)) //
				.method(not(isStatic()) //
						.and(not(isFinal())) //
						.and(not(isDeclaredBy(Object.class))) //
						.and(not(named(getDelegateMethodname))) //
						.and(not(named(getRecorderMethodname))) //
				) //
				.intercept(Advice.to(RecordingAdvice.class) //
						.wrap(MethodDelegation.toField(FIELDNAME_DELEGATE)) //
				) //
				.make() //
				.load(type.getClassLoader(), ClassLoadingStrategy.Default.INJECTION) //
				.getLoaded() //
		;
	}

	private static <T> Constructor<?> getConstructor(Class<T> type) {
		return stream(type.getConstructors()).findFirst().or(() -> //
		stream(type.getDeclaredConstructors()).findFirst().map(c -> makeAccessible(c)))
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("Class %s must declare at least one accessible constructor", type.getName())));
	}

	private static Constructor<?> makeAccessible(Constructor<?> c) {
		c.setAccessible(true);
		return c;
	}

	public static <T> T like(T object) {
		return spec(isSpec(object) ? delegate(object) : object, withSettings().lenient());
	}

	/**
	 * Returns the recorded invocation details of a DTO proxy.
	 *
	 * @param object the proxy instance returned by {@link #spec(Object)}
	 * @return details of method invocations and matchers
	 * @throws IllegalArgumentException if the object was not intercepted by
	 *                                  {@link #spec(Object)}
	 */
	public static InvocationDetails invocations(Object object) {
		return new DefaultInvocationDetails(recorder(object).invocations());
	}

	/**
	 * Returns the pacto settings of a DTO proxy.
	 *
	 * @param object the proxy instance returned by {@link #spec(Object)}
	 * @return settings that where passed when creating the pacto spec
	 * @throws IllegalArgumentException if the object was not intercepted by
	 *                                  {@link #spec(Object)}
	 */
	public static PactoSettingsImpl settings(Object object) {
		PactoSettings settings = recorder(object).settings();
		Class<PactoSettingsImpl> expectedClass = PactoSettingsImpl.class;
		if (!expectedClass.isInstance(settings)) {
			throw new IllegalArgumentException("Unexpected implementation of '" + settings.getClass().getCanonicalName()
					+ "'\n" + "At the moment, you cannot provide your own implementations that class.");
		}
		return expectedClass.cast(settings);
	}

	public static Recorder recorder(Object object) {
		if (!(object instanceof HasRecorder)) {
			throw new IllegalArgumentException(
					format("%s of type (%s) not intercepted", object, object.getClass().getName()));
		}
		return ((HasRecorder) object).__pacto_recorder();
	}

	/**
	 * Returns the original DTO object behind a proxy.
	 *
	 * @param object the proxy instance returned by {@link #spec(Object)}
	 * @param <T>    the type of the DTO
	 * @return the original DTO object, or the object itself if not proxied
	 */
	@SuppressWarnings("unchecked")
	public static <T> T delegate(T object) {
		return isSpec(object) ? (T) ((HasDelegate<?>) object).__pacto_delegate() : object;
	}

	/**
	 * Returns the <code>true</code> if this is a DTO object behind a proxy.
	 *
	 * @param object the proxy instance returned by {@link #spec(Object)}
	 * @return <code>true</code> if the object itself is proxied
	 */
	public static boolean isSpec(Object object) {
		return object instanceof HasDelegate<?>;
	}

}
