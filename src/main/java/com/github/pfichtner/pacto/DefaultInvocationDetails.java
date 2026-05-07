package com.github.pfichtner.pacto;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DefaultInvocationDetails implements InvocationDetails {

	List<Invocation> invocations;

	public DefaultInvocationDetails(List<Invocation> invocations) {
		this.invocations = List.copyOf(invocations);
	}

}
