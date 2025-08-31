package com.github.pfichtner.pacto.matchers;

public class EachLikeArg extends PactoMatcher<Object> {

	private final Object value;

	public EachLikeArg(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.format("eachLike(%s)", value);
	}

}
