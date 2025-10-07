package com.github.pfichtner.pacto.matchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.github.pfichtner.pacto.testdata.Foo;

public class TestTarget {

	@SuppressWarnings("unused")
	private static final List<String> someData1 = List.of();
	static final List<String> someData2 = List.of("1");
	protected static final List<String> someData3 = List.of("1", "2", "3");
	public static final List<String> someData4 = List.of("1", "2", "3", "4", "5");

	public void objectArg(Object value) {
	}

	void stringArg(String value) {
	}

	void floatArg(float value) {
	}

	void floatWrapperArg(Float value) {
	}

	void doubleArg(double value) {
	}

	void doubleWrapperArg(Double value) {
	}

	void intArg(int value) {
	}

	void integerWrapperArg(Integer value) {
	}

	void longArg(long value) {
	}

	void longWrapperArg(Long value) {
	}

	void numberArg(Number value) {
	}

	void uuidArg(UUID value) {
	}

	void dateArg(Date value) {
	}

	void localDateArg(LocalDate value) {
	}

	void localDateTimeArg(LocalDateTime value) {
	}

	void arrayArg(Foo[] array) {
	}

	void listArg(List<Foo> list) {
	}

	void setArg(Set<Foo> list) {
	}

}
