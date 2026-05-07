package com.github.pfichtner.pacto;

import java.lang.reflect.Method;

import com.github.pfichtner.pacto.matchers.PactoMatcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.With;
import lombok.experimental.Accessors;

@ToString
@AllArgsConstructor
@Accessors(fluent = true)
@Getter
@With
final class InvocationStub implements Invocation {

	public static InvocationStub invocation(Object value) {
		return new InvocationStub(value);
	}

	private final String attribute;
	private final Class<?> type;
	private final Object arg;
	private final PactoMatcher<?> matcher;

	private InvocationStub(Object value) {
		this("testAttribute", value.getClass(), value, null);
	}

	@Override
	public Method method() {
		return null;
	}

	@Override
	public Object delegate() {
		return null;
	}

}