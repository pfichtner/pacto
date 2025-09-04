package com.github.pfichtner.pacto;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import com.github.pfichtner.pacto.matchers.PactoMatcher;

public class DefaultInvocation implements Invocation {

	private final Object delegate;
	private final Method method;
	private final Object arg;
	private final Object result;
	private final PactoMatcher<?> matcher;

	public DefaultInvocation(Object delegate, Method method, Object arg, Object result, PactoMatcher<?> matcher) {
		this.delegate = delegate;
		this.method = method;
		this.arg = arg;
		this.result = result;
		this.matcher = matcher;
	}

	@Override
	public Object delegate() {
		return delegate;
	}

	@Override
	public Method method() {
		return method;
	}

	@Override
	public Object arg() {
		return arg;
	}

	@Override
	public PactoMatcher<?> matcher() {
		return matcher;
	}

	@Override
	public int hashCode() {
		return Objects.hash(arg, delegate, matcher, method, result);
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
			return Optional.of(Character.toLowerCase(property.charAt(0)) + property.substring(1));
		}
		return Optional.empty();
	}

	@Override
	public Class<?> type() {
		return method().getParameterTypes()[0];
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultInvocation other = (DefaultInvocation) obj;
		return Objects.equals(arg, other.arg) && Objects.equals(delegate, other.delegate)
				&& Objects.equals(matcher, other.matcher) && Objects.equals(method, other.method)
				&& Objects.equals(result, other.result);
	}

	@Override
	public String toString() {
		return "DefaultInvocation [delegate=" + delegate + ", method=" + method + ", arg=" + arg + ", result=" + result
				+ ", matcher=" + matcher + "]";
	}

}
