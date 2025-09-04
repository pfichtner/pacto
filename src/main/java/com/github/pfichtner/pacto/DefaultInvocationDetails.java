package com.github.pfichtner.pacto;

import java.util.List;

public class DefaultInvocationDetails implements InvocationDetails {

	private final List<Invocation> invocations;

	public DefaultInvocationDetails(List<Invocation> invocations) {
		this.invocations = List.copyOf(invocations);
	}

	@Override
	public List<Invocation> invocations() {
		return List.copyOf(invocations);
	}

}
