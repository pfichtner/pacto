package com.github.pfichtner.pacto;

import java.lang.reflect.Method;

import com.github.pfichtner.pacto.matchers.PactoMatcher;

public interface Invocation {

	PactoMatcher<?> matcher();

	Object arg();

	Method method();

	Object delegate();

	String attribute();

	Class<?> type();

}
