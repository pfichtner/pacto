package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeArg extends PactoMatcher<String> {

	private final Date example;

	public TimeArg(String format, Date example) {
		super(format);
		this.example = example;
		withToStringFormat(format("time(%%s,%s)", new SimpleDateFormat(value()).format(example)));
	}

	public Date example() {
		return example;
	}

}
