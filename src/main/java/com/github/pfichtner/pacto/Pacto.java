package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.RecordingAdvice.FIELDNAME_DELEGATE;
import static com.github.pfichtner.pacto.RecordingAdvice.FIELDNAME_RECORDER;
import static com.github.pfichtner.pacto.RecordingAdvice.copyFields;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.isFinal;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;

import java.lang.reflect.Constructor;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.FieldPersistence;
import net.bytebuddy.description.modifier.SyntheticState;
import net.bytebuddy.description.modifier.Visibility;
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

	/**
	 * Wraps a DTO with a proxy that records method invocations and matcher
	 * arguments.
	 *
	 * @param intercept the original DTO to wrap
	 * @param <T>       the type of the DTO
	 * @return a proxy instance of the DTO that records matcher calls
	 * @throws RuntimeException if proxy creation fails
	 */
	public static <T> T spec(T intercept) {
		try {
			@SuppressWarnings("unchecked")
			Class<T> type = (Class<T>) intercept.getClass();
			Recorder recorder = new Recorder();
			Constructor<? extends T> constructor = proxyClass(type).getDeclaredConstructor(type, recorder.getClass());
			T interceptable = constructor.newInstance(intercept, recorder);
			copyFields(intercept, interceptable);
			return interceptable;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> Class<? extends T> proxyClass(Class<T> type) throws NoSuchMethodException, SecurityException {
		String getDelegateMethodname = HasDelegate.class.getMethod("__pacto_delegate").getName();
		String getRecorderMethodname = HasRecorder.class.getMethod("__pacto_recorder").getName();
		return new ByteBuddy() //
				.with(new NamingStrategy.SuffixingRandom("PactoProxy")) //
				.subclass(type) //
				.implement(HasDelegate.class, HasRecorder.class) //
				.defineField(FIELDNAME_DELEGATE, type, Visibility.PRIVATE, FieldManifestation.FINAL,
						FieldPersistence.TRANSIENT) //
				.defineField(FIELDNAME_RECORDER, Recorder.class, Visibility.PRIVATE, FieldManifestation.FINAL,
						FieldPersistence.TRANSIENT) //
				.defineConstructor(Visibility.PUBLIC).withParameters(type, Recorder.class).intercept( //
						MethodCall.invoke(type.getDeclaredConstructor()) //
								.andThen(FieldAccessor.ofField(FIELDNAME_DELEGATE).setsArgumentAt(0)) //
								.andThen(FieldAccessor.ofField(FIELDNAME_RECORDER).setsArgumentAt(1)) //
				) //
				.defineMethod(getDelegateMethodname, type, Visibility.PUBLIC, SyntheticState.SYNTHETIC) //
				.intercept(FieldAccessor.ofField(FIELDNAME_DELEGATE)) //
				.defineMethod(getRecorderMethodname, Recorder.class, Visibility.PUBLIC, SyntheticState.SYNTHETIC) //
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

	/**
	 * Returns the recorded invocation details of a DTO proxy.
	 *
	 * @param object the proxy instance returned by {@link #spec(Object)}
	 * @return details of method invocations and matchers
	 * @throws IllegalArgumentException if the object was not intercepted by
	 *                                  {@link #spec(Object)}
	 */
	public static InvocationDetails invocations(Object object) {
		if (!(object instanceof HasRecorder)) {
			throw new IllegalArgumentException(object + " not intercepted");
		}
		Recorder recorder = ((HasRecorder) object).__pacto_recorder();
		return new DefaultInvocationDetails(recorder.invocations());
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
