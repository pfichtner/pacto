package com.github.pfichtner.pacto;

import java.util.List;

public class DefaultInvocationDetails implements InvocationDetails {

	private final List<Invocation> invocations;

	public DefaultInvocationDetails(List<Invocation> invocations) {
		this.invocations = invocations;
	}

	@Override
	public List<Invocation> getAllInvocations() {
		return invocations;
	}

}
