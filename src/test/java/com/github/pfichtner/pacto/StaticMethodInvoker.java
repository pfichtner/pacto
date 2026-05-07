package com.github.pfichtner.pacto;

import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Comparator.comparing;
import static lombok.AccessLevel.PRIVATE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Utility class that reflects over a given "matcher" class and invokes all
 * non-private static methods in a deterministic order. The results of these
 * method invocations are then passed to matching methods on a target object.
 *
 * <p>
 * The workflow is as follows:
 * </p>
 * <ol>
 * <li>All declared methods of {@code matcherClass} are sorted by name and
 * parameter types.</li>
 * <li>Each static, non-private method is invoked with arguments provided by an
 * {@link ArgumentProvider}.</li>
 * <li>If the invoked method returns a non-null result, that result is passed to
 * compatible methods on the {@code target} object.</li>
 * <li>Target methods are selected if they accept exactly one parameter and the
 * parameter type is assignable from the result type (including
 * primitive-wrapper compatibility).</li>
 * </ol>
 */
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class StaticMethodInvoker {

	public interface ArgumentProvider {
		Object getArgument(String methodName, Class<?> type);
	}

	Class<?> matcherClass;
	Object target;
	ArgumentProvider argumentProvider;

	public void invoke() throws Exception {
		for (Method method : sort(matcherClass.getDeclaredMethods())) {
			if (isStaticNonPrivate(method)) {
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

	private static boolean isStaticNonPrivate(Method method) {
		return isStaticNonPrivate(method.getModifiers());
	}

	private static boolean isStaticNonPrivate(int modifiers) {
		return isStatic(modifiers) && !isPrivate(modifiers);
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
