package com.github.pfichtner.pacto.matchers;

public class EachLikeArg extends PactoMatcher<Object> {

	private Integer max;
	private Integer min;

	public EachLikeArg(Object value) {
		super(value);
	}

	public EachLikeArg max(int max) {
		this.max = max;
		return this;
	}

	public Integer max() {
		return max;
	}

	public EachLikeArg min(int min) {
		this.min = min;
		return this;
	}

	public Integer min() {
		return min;
	}

	@Override
	public String toString() {
		if (min != null && max != null) {
			return String.format("minMaxArrayLike(%s, %d, %d)", value(), min, max);
		} else if (max != null) {
			return String.format("maxArrayLike(%s, %d)", value(), max);
		} else if (min != null) {
			return String.format("minArrayLike(%s, %d)", value(), min);
		} else {
			return String.format("eachLike(%s)", value());
		}
	}

}
