package com.github.pfichtner.pacto;

import java.util.ArrayList;
import java.util.List;

import com.github.pfichtner.pacto.matchers.PactoMatcher;

public class MatcherRegistry {

	private static final ThreadLocal<List<PactoMatcher<?>>> registry = ThreadLocal.withInitial(ArrayList::new);

	public static void register(PactoMatcher<?> matcher) {
		registry.get().add(matcher);
	}

	public static List<PactoMatcher<?>> pullMatchers() {
		var matchers = List.copyOf(registry.get());
		reset();
		return matchers;
	}

	public static void reset() {
		registry.get().clear();
	}
}
