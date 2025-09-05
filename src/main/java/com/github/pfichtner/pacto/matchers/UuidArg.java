package com.github.pfichtner.pacto.matchers;

import java.util.UUID;

public class UuidArg extends PactoMatcher<UUID> {

	public UuidArg(UUID in) {
		super(in);
		withToStringFormat("uuid(%s)");
	}

}
