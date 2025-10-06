package com.github.pfichtner.pacto;

import static java.util.stream.IntStream.range;

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
		List<PactoMatcher<?>> matchers = MatcherRegistry.popMatchers();
		range(0, args.length)
				.mapToObj(i -> new DefaultInvocation(delegate, method, matcher(matchers, i), args[i], result))
				.forEach(invocations::add);
	}

	private static PactoMatcher<?> matcher(List<PactoMatcher<?>> matchers, int index) {
		return index < matchers.size() ? matchers.get(index) : null;
	}

	public PactoSettings settings() {
		return settings;
	}

	public List<Invocation> invocations() {
		return List.copyOf(invocations);
	}

}
