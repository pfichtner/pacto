package com.github.pfichtner.pacto;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.isFinal;
import static net.bytebuddy.matcher.ElementMatchers.isProtected;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

import java.util.IdentityHashMap;
import java.util.Map;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;

public class Pacto {

	private static Map<Object, DelegateInterceptor> interceptors = new IdentityHashMap<>();

	public static <T> T spec(T delegate) {
		try {
			@SuppressWarnings("unchecked")
			Class<T> type = (Class<T>) delegate.getClass();
			DelegateInterceptor interceptor = new DelegateInterceptor(delegate, new Recorder());
			Class<? extends T> proxyClass = proxyClass(delegate, type, interceptor);
			T intercepted = proxyClass.getDeclaredConstructor().newInstance();
			interceptors.put(intercepted, interceptor);
			return intercepted;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> Class<? extends T> proxyClass(T delegate, Class<T> type,
			DelegateInterceptor delegateInterceptor) {
		return new ByteBuddy() //
				.subclass(type) //
				.method(not(isStatic()) //
						.and(not(isFinal())) //
						.and(not(isDeclaredBy(Object.class))) //
				).intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(delegateInterceptor))) //
				.make() //
				.load(delegate.getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER) //
				.getLoaded();
	}

	public static InvocationDetails invocations(Object object) {
		DelegateInterceptor interceptor = interceptor(object);
		if (interceptor == null) {
			throw new IllegalArgumentException(object + " not intercepted");
		}
		return new DefaultInvocations(interceptor);
	}

	@SuppressWarnings("unchecked")
	public static <T> T delegate(T object) {
		DelegateInterceptor interceptor = interceptor(object);
		return interceptor == null ? object : (T) interceptor.getDelegate();
	}

	private static DelegateInterceptor interceptor(Object object) {
		return interceptors.get(object);
	}

}
