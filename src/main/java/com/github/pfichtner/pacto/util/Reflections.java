package com.github.pfichtner.pacto.util;

import static java.lang.reflect.Modifier.isFinal;

import java.lang.reflect.Field;

public final class Reflections {

	private Reflections() {
		super();
	}

	public static <T> T copyFields(Object source, T target) throws IllegalAccessException {
		Class<?> clazz = source.getClass();
		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				if (!isFinal(field.getModifiers())) {
					field.setAccessible(true);
					field.set(target, field.get(source));
				}
			}
			clazz = clazz.getSuperclass();
		}
		return target;
	}

}
