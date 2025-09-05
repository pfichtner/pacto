package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeArg extends PactoMatcher<String> {

	private final Date example;

	public TimeArg(String format, Date example) {
		super(format);
		this.example = example;
	}

	public Date example() {
		return example;
	}

	@Override
	public String toString() {
		var formatter = new SimpleDateFormat(value());
		return format("time(%s,%s)", value(), formatter.format(example));
	}

}
