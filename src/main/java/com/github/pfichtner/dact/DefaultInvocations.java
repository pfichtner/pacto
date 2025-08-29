package com.github.pfichtner.dact;

import static java.util.Collections.emptyList;

import java.util.List;

public class DefaultInvocations implements InvocationDetails {

	private final List<Invocation> invocations;

	public DefaultInvocations(DelegateInterceptor interceptor) {
		Recorder recorder = interceptor.getRecorder();
		invocations = recorder == null ? emptyList() : recorder.getInvocations();
	}

	@Override
	public List<Invocation> getAllInvocations() {
		return invocations;
	}

}
