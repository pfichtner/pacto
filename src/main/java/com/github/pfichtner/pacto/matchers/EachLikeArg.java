package com.github.pfichtner.pacto.matchers;

public class EachLikeArg extends PactoMatcher<Object> {

	private final Object value;
	private Integer max;
	private Integer min;

	public EachLikeArg(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public EachLikeArg max(int max) {
		this.max = max;
		return this;
	}

	public Integer getMax() {
		return max;
	}

	public EachLikeArg min(int min) {
		this.min = min;
		return this;
	}

	public Integer getMin() {
		return min;
	}

	@Override
	public String toString() {
		if (max != null) {
			return String.format("maxArrayLike(%s, %d)", value, max);
		}
		if (min != null) {
			return String.format("minArrayLike(%s, %d)", value, min);
		}
		return String.format("eachLike(%s)", value);
	}

}
