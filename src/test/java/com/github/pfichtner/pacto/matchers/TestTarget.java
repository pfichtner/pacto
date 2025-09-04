package com.github.pfichtner.pacto.matchers;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.github.pfichtner.pacto.testdata.Foo;

public class TestTarget {

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

	public void uuidArg(UUID value) {
	}

	void arrayArg(Foo[] array) {
	}

	void listArg(List<Foo> list) {
	}

	void setArg(Set<Foo> list) {
	}

}
