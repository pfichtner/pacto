package com.github.pfichtner.pacto;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.github.pfichtner.pacto.matchers.PactoMatcher;

public class Recorder {

	private final List<Invocation> invocations = new ArrayList<>();
	private final PactoSettings settings;

	public Recorder(PactoSettings settings) {
		this.settings = settings;
	}

	public void recordInterception(Object delegate, Method method, Object[] args, Object result) {
		List<PactoMatcher<?>> matchers = MatcherRegistry.pullMatchers();
		for (int i = 0; i < args.length; i++) {
			PactoMatcher<?> matcher = i < matchers.size() ? matchers.get(i) : null;
			invocations.add(new DefaultInvocation(delegate, method, args[i], result, matcher));
		}
	}

	public PactoSettings settings() {
		return settings;
	}

	public List<Invocation> invocations() {
		return List.copyOf(invocations);
	}

}
