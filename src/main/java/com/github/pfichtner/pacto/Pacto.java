package com.github.pfichtner.pacto;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.isFinal;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.FieldPersistence;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType.NamingStrategy;

public class Pacto {

	private static record Data(Object intercept, Recorder recorder) {
	}

	private static Map<Object, Data> data = new IdentityHashMap<>();

	public static <T> T spec(T intercept) {
		try {
			@SuppressWarnings("unchecked")
			Class<T> type = (Class<T>) intercept.getClass();
			Class<? extends T> proxyClass = proxyClass(type);

			Recorder recorder = new Recorder();
			T interceptable = proxyClass.getDeclaredConstructor(type, Recorder.class).newInstance(intercept, recorder);
			data.put(interceptable, new Data(intercept, recorder));
			copyData(intercept, interceptable);
			return interceptable;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void copyData(Object source, Object target) throws IllegalAccessException {
		for (Field field : source.getClass().getDeclaredFields()) {
			if (field.getDeclaringClass() != Object.class) {
				field.setAccessible(true);
				field.set(target, field.get(source));
			}
		}
	}

	private static <T> Class<? extends T> proxyClass(Class<T> type) throws NoSuchMethodException, SecurityException {
		return new ByteBuddy() //
				.with(new NamingStrategy.SuffixingRandom("ByteBuddySubclass")) //
				.subclass(type) //
				.defineField("delegate", type, Visibility.PRIVATE, FieldManifestation.FINAL, FieldPersistence.TRANSIENT) //
				.defineField("recorder", Recorder.class, Visibility.PRIVATE, FieldManifestation.FINAL,
						FieldPersistence.TRANSIENT) //
				.defineConstructor(Visibility.PUBLIC).withParameters(type, Recorder.class).intercept( //
						MethodCall.invoke(type.getDeclaredConstructor()) //
								.andThen(FieldAccessor.ofField("delegate").setsArgumentAt(0)) //
								.andThen(FieldAccessor.ofField("recorder").setsArgumentAt(1)) //
				) //
				.method(not(isStatic()) //
						.and(not(isFinal())) //
						.and(not(isDeclaredBy(Object.class))) //
				) //
				.intercept(Advice.to(RecordingAdvice.class) //
						.wrap(MethodDelegation.toField("delegate")) //
				) //
				.make() //
				.load(type.getClassLoader(), ClassLoadingStrategy.Default.INJECTION) //
				.getLoaded() //
		;
	}

	public static InvocationDetails invocations(Object object) {
		Data data = interceptor(object);
		if (data == null) {
			throw new IllegalArgumentException(object + " not intercepted");
		}
		return new DefaultInvocationDetails(data.recorder().getInvocations());
	}

	@SuppressWarnings("unchecked")
	public static <T> T delegate(T object) {
		Data pair = interceptor(object);
		return pair == null ? object : (T) pair.intercept;
	}

	private static Data interceptor(Object object) {
		return data.get(object);
	}

}
