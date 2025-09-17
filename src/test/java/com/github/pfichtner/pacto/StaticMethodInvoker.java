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

	public StaticMethodInvoker(Class<?> matcherFactory, Object target, ArgumentProvider argumentProvider) {
		this.matcherClass = matcherFactory;
		this.target = target;
		this.argumentProvider = argumentProvider;
	}

	public void invoke() throws Exception {
		for (Method method : sort(matcherClass.getDeclaredMethods())) {
			if (isStatic(method.getModifiers()) && !isPrivate(method.getModifiers())) {
				Object[] args = Arrays.stream(method.getParameterTypes())
						.map(t -> argumentProvider.getArgument(method.getName(), t)).toArray();
				Object result;
				try {
					result = method.invoke(null, args);
				} catch (IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(
							"Error invoking static " + method + " with args " + Arrays.toString(args), e);
				}
				if (result != null) {
					callTargetMethods(result);
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

	private void callTargetMethods(Object value) {
		for (Method method : sort(target.getClass().getDeclaredMethods())) {
			Class<?>[] paramTypes = method.getParameterTypes();
			if (paramTypes.length == 1 && isAssignable(paramTypes[0], value.getClass())) {
				method.setAccessible(true);
				try {
					method.invoke(target, value);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException("Error invoking " + method + " on " + target + " with value " + value,
							e);
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
