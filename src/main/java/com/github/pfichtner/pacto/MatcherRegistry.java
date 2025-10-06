package com.github.pfichtner.pacto;

import java.util.ArrayList;
import java.util.List;

import com.github.pfichtner.pacto.matchers.PactoMatcher;

public class MatcherRegistry {

	private static final ThreadLocal<List<PactoMatcher<?>>> registry = ThreadLocal.withInitial(ArrayList::new);

	public static void register(PactoMatcher<?> matcher) {
		registry().add(matcher);
	}

	public static List<PactoMatcher<?>> popMatchers() {
		try {
			return pullMatchers();
		} finally {
			reset();
		}
	}

	public static List<PactoMatcher<?>> pullMatchers() {
		return List.copyOf(registry());
	}

	public static void reset() {
		registry().clear();
	}

	private static List<PactoMatcher<?>> registry() {
		return registry.get();
	}
}
