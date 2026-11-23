package com.github.pfichtner.pacto;

import static java.lang.Character.toLowerCase;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import com.github.pfichtner.pacto.matchers.PactoMatcher;

import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
public final class DefaultInvocation implements Invocation {

	Object delegate;
	Method method;
	PactoMatcher<?> matcher;
	Object arg;
	Object result;

	@Override
	public Class<?> type() {
		return method().getParameterTypes()[0];
	}

	@Override
	public String attribute() {
		try {
			return propertyName(method).orElse(method.getName());
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	private static Optional<String> propertyName(Method writeMethod) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(writeMethod.getDeclaringClass());
		return Arrays.stream(beanInfo.getPropertyDescriptors()) //
				.filter(d -> writeMethod.equals(d.getWriteMethod())) //
				.map(PropertyDescriptor::getName) //
				.findFirst() //
				.or(() -> propertyNameOfFluentSetter(writeMethod));
	}

	private static Optional<String> propertyNameOfFluentSetter(Method writeMethod) {
		if (writeMethod.getName().length() > 3 //
				&& writeMethod.getName().startsWith("set") //
				&& writeMethod.getParameterCount() == 1 //
				&& writeMethod.getReturnType().isAssignableFrom(writeMethod.getDeclaringClass())) {
			String property = writeMethod.getName().substring(3);
			return Optional.of(toLowerCase(property.charAt(0)) + property.substring(1));
		}
		return Optional.empty();
	}

}
