package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

import java.util.UUID;

public class UuidArg extends PactoMatcher<UUID> {

	public UuidArg(UUID in) {
		super(in);
	}

	@Override
	public String toString() {
		return format("uuid(%s)", value());
	}

}
