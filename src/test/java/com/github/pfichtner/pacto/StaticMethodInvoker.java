package com.github.pfichtner.pacto;

import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Comparator.comparing;

import java.lang.reflect.InvocationTargetException;
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
		for (Method method : sort(matcherClass.getDeclaredMethods())) {
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

	private static Method[] sort(Method[] values) {
		return Arrays.stream(values) //
				.sorted(comparing(Method::getName) //
						.thenComparing(m -> Arrays.toString(m.getParameterTypes()))) //
				.toArray(Method[]::new);
	}

	private void callTargetMethods(Object value)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Method method : sort(target.getClass().getDeclaredMethods())) {
			Class<?>[] paramTypes = method.getParameterTypes();
			if (paramTypes.length == 1 && isAssignable(paramTypes[0], value.getClass())) {
				method.setAccessible(true);
				method.invoke(target, value);
			}
		}
	}

	private boolean isAssignable(Class<?> paramType, Class<?> valueType) {
		return (paramType.isAssignableFrom(valueType)) //
				|| (paramType.isPrimitive() && wrap(paramType).isAssignableFrom(valueType)) //
				|| (valueType.isPrimitive() && paramType.isAssignableFrom(wrap(valueType)));
	}

	private static Class<?> wrap(Class<?> type) {
		if (!type.isPrimitive()) {
			return type;
		} else if (type == int.class) {
			return Integer.class;
		} else if (type == long.class) {
			return Long.class;
		} else if (type == double.class) {
			return Double.class;
		} else if (type == float.class) {
			return Float.class;
		} else if (type == boolean.class) {
			return Boolean.class;
		} else if (type == char.class) {
			return Character.class;
		} else if (type == byte.class) {
			return Byte.class;
		} else if (type == short.class) {
			return Short.class;
		} else {
			return type;
		}
	}

}
