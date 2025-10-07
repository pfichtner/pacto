package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

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
			return format("minMaxArrayLike(%s, %d, %d)", value(), min, max);
		} else if (max != null) {
			return format("maxArrayLike(%s, %d)", value(), max);
		} else if (min != null) {
			return format("minArrayLike(%s, %d)", value(), min);
		} else {
			return format("eachLike(%s)", value());
		}
	}

}
