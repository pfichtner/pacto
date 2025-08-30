package com.github.pfichtner.pacto.matchers;

import org.mockito.ArgumentMatcher;

public class PactoMatcher<T> implements ArgumentMatcher<T> {

	@Override
	public boolean matches(T argument) {
		throw new IllegalStateException("not implemented");
	}

}
