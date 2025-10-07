package com.github.pfichtner.pacto.matchers;

import static java.lang.String.format;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatetimeArg extends PactoMatcher<String> {

	private final Date example;

	public DatetimeArg(String format, Date example) {
		super(format);
		this.example = example;
		withToStringFormat(format("datetime(%%s,%s)", new SimpleDateFormat(value()).format(example)));
	}

	public Date example() {
		return example;
	}

}
