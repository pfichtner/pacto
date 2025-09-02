package com.github.pfichtner.pacto;

import java.lang.reflect.Method;

import org.mockito.ArgumentMatcher;

public interface Invocation {

	ArgumentMatcher<?> matcher();

	Object arg();

	Method method();

	Object delegate();

	String attribute();

	Class<?> type();

}
