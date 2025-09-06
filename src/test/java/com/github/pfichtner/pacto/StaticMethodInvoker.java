package com.github.pfichtner.pacto;

import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Method;
import java.util.Arrays;

class StaticMethodInvoker {

	public interface ArgumentProvider {
		Object getArgument(String methodName, Class<?> type);
	}

	private final Class<?> matcherClass;
	private final Object target;
	private final ArgumentProvider argumentProvider;

	public StaticMethodInvoker(Class<?> matcherClass, Object target, ArgumentProvider argumentProvider) {
		this.matcherClass = matcherClass;
		this.target = target;
		this.argumentProvider = argumentProvider;
	}

	public void invoke() throws Exception {
		for (Method method : matcherClass.getDeclaredMethods()) {
			if (isStatic(method.getModifiers()) && !isPrivate(method.getModifiers())) {
				Object[] args = Arrays.stream(method.getParameterTypes())
						.map(t -> argumentProvider.getArgument(method.getName(), t)).toArray();

				try {
					Object result = method.invoke(null, args);

					if (result != null) {
						callTargetMethods(result);
					}
				} catch (Exception e) {
					System.err.println("Error invoking " + method + ": " + e.getMessage());
				}
			}
		}
	}

	private void callTargetMethods(Object value) {
		for (Method method : target.getClass().getDeclaredMethods()) {
			Class<?>[] paramTypes = method.getParameterTypes();
			if (paramTypes.length != 1)
				continue;

			if (isAssignable(paramTypes[0], value.getClass())) {
				try {
					method.setAccessible(true);
					method.invoke(target, value);
				} catch (Exception e) {
					System.err.println("Failed calling " + method.getName() + " with " + value + ": " + e);
				}
			}
		}
	}

	private boolean isAssignable(Class<?> paramType, Class<?> valueType) {
		return (paramType.isAssignableFrom(valueType)) //
				|| (paramType.isPrimitive() && wrap(paramType).isAssignableFrom(valueType)) //
				|| (valueType.isPrimitive() && paramType.isAssignableFrom(wrap(valueType)));
	}

	private static Class<?> wrap(Class<?> type) {
		if (!type.isPrimitive())
			return type;
		if (type == int.class)
			return Integer.class;
		if (type == long.class)
			return Long.class;
		if (type == double.class)
			return Double.class;
		if (type == float.class)
			return Float.class;
		if (type == boolean.class)
			return Boolean.class;
		if (type == char.class)
			return Character.class;
		if (type == byte.class)
			return Byte.class;
		if (type == short.class)
			return Short.class;
		return type;
	}

}
