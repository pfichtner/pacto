package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class MatchUrlArg extends PactoMatcher<String> {

	private final String[] pathFragments;

	public MatchUrlArg(String basePath, String... pathFragments) {
		super(basePath);
		this.pathFragments = pathFragments.clone();
	}

	public Object[] pathFragments() {
		return pathFragments.clone();
	}

	@Override
	public String toString() {
		return pathFragments.length == 0 //
				? format("matchUrl(%s)", value()) //
				: format("matchUrl(%s/%s)", value(), String.join("/", pathFragments));
	}

}
