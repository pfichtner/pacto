package com.github.pfichtner.dact;

import static java.util.stream.Collectors.toList;
import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.LocalizedMatcher;

public class Recorder {

	private final List<Invocation> invocations = new ArrayList<>();

	public void recordInterception(Object delegate, Method method, Object[] args, Object result) {
		List<LocalizedMatcher> matchers = currentMatchers();
		invocations.addAll(IntStream.range(0, args.length).mapToObj(i -> {
			Object arg = args[i];
			if (i < matchers.size()) {
				ArgumentMatcher<?> matcher = matchers.get(i).getMatcher();
				if (matcher != null) {
					return new DefaultInvocation(delegate, method, arg, result, matcher);
				}
			}
			return new DefaultInvocation(delegate, method, arg, result, null);
		}).collect(toList()));
	}

	public List<Invocation> getInvocations() {
		return invocations;
	}

	private static List<LocalizedMatcher> currentMatchers() {
		return mockingProgress().getArgumentMatcherStorage().pullLocalizedMatchers();
	}

}
