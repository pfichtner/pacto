package com.github.pfichtner.pacto;

import java.util.List;

public class DefaultInvocations implements InvocationDetails {

	private final List<Invocation> invocations;

	public DefaultInvocations(List<Invocation> list) {
		invocations = list;
	}

	@Override
	public List<Invocation> getAllInvocations() {
		return invocations;
	}

}
