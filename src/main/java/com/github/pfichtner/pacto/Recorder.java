package com.github.pfichtner.pacto;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.github.pfichtner.pacto.matchers.PactoMatcher;

public class Recorder {

	private final List<Invocation> invocations = new ArrayList<>();

	public void recordInterception(Object delegate, Method method, Object[] args, Object result) {
	    List<PactoMatcher<?>> matchers = MatcherRegistry.pullMatchers();

	    for (int i = 0; i < args.length; i++) {
	        Object arg = args[i];
	        PactoMatcher<?> matcher = i < matchers.size() ? matchers.get(i) : null;

	        DefaultInvocation invocation = new DefaultInvocation(delegate, method, arg, result, matcher);
	        invocations.add(invocation);
	    }
	}

	public List<Invocation> getInvocations() {
		return invocations;
	}

}
