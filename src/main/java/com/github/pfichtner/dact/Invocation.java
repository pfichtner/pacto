package com.github.pfichtner.dact;

import java.lang.reflect.Method;

import org.mockito.ArgumentMatcher;

public interface Invocation {

	ArgumentMatcher<?> getMatcher();

	Object getArg();

	Method getMethod();

	Object getDelegate();

	String attribute();

	Class<?> type();

	Object getResult();

}
