package com.github.pfichtner.pacto;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import org.mockito.ArgumentMatcher;

public class DefaultInvocation implements Invocation {

	private final Object delegate;
	private final Method method;
	private final Object arg;
	private final Object result;
	private final ArgumentMatcher<?> matcher;

	public DefaultInvocation(Object delegate, Method method, Object arg, Object result, ArgumentMatcher<?> matcher) {
		this.delegate = delegate;
		this.method = method;
		this.arg = arg;
		this.result = result;
		this.matcher = matcher;
	}

	@Override
	public Object getDelegate() {
		return delegate;
	}

	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public Object getArg() {
		return arg;
	}

	@Override
	public ArgumentMatcher<?> getMatcher() {
		return matcher;
	}

	@Override
	public int hashCode() {
		return Objects.hash(arg, delegate, matcher, method, result);
	}

	@Override
	public String getAttribute() {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(method.getDeclaringClass());
			return isSetter(beanInfo, method) ? propertyName(beanInfo, method) : method.getName();
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	private static String propertyName(BeanInfo beanInfo, Method method) {
		return Arrays.stream(beanInfo.getPropertyDescriptors()) //
				.filter(d -> method.equals(d.getWriteMethod()) || method.equals(d.getReadMethod())) //
				.map(PropertyDescriptor::getName).findFirst().orElse(null);
	}

	private static boolean isSetter(BeanInfo beanInfo, Method method) throws IntrospectionException {
		return propertyName(beanInfo, method) != null && method.equals(getWriteMethod(beanInfo, method));
	}

	private static Method getWriteMethod(BeanInfo beanInfo, Method method) {
		return Arrays.stream(beanInfo.getPropertyDescriptors()).map(PropertyDescriptor::getWriteMethod)
				.filter(Objects::nonNull).filter(m -> m.equals(method)).findFirst().orElse(null);
	}

	@Override
	public Class<?> getType() {
		return getMethod().getParameterTypes()[0];
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
