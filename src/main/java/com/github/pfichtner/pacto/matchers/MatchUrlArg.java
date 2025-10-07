package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

public class MatchUrlArg extends PactoMatcher<String> {

	private final String[] pathFragments;

	public MatchUrlArg(String basePath, String... pathFragments) {
		super(basePath);
		this.pathFragments = pathFragments.clone();
		if (pathFragments.length == 0) {
			withToStringFormat("matchUrl(%s)");
		} else {
			withToStringFormat(format("matchUrl(%%s/%s)", String.join("/", pathFragments)));
		}
	}

	public Object[] pathFragments() {
		return pathFragments.clone();
	}

}
