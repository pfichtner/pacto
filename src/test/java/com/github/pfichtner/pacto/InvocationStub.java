package com.github.pfichtner.pacto;

import java.lang.reflect.Method;

import com.github.pfichtner.pacto.matchers.PactoMatcher;

import lombok.ToString;

@ToString
final class InvocationStub implements Invocation {

	public static InvocationStub invocation(Object value) {
		return new InvocationStub(value);
	}

	private final String attribute;
	private final Class<?> type;
	private final Object arg;
	private PactoMatcher<?> matcher;

	private InvocationStub(Object value) {
		this.attribute = "testAttribute";
		this.type = value.getClass();
		this.arg = value;
	}

	public InvocationStub withMatcher(PactoMatcher<?> matcher) {
		this.matcher = matcher;
		return this;
	}

	@Override
	public PactoMatcher<?> matcher() {
		return matcher;
	}

	@Override
	public Object arg() {
		return arg;
	}

	@Override
	public Method method() {
		return null;
	}

	@Override
	public Object delegate() {
		return null;
	}

	@Override
	public String attribute() {
		return attribute;
	}

	@Override
	public Class<?> type() {
		return type;
	}
}