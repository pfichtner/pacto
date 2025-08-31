package com.github.pfichtner.pacto.matchers;

import static com.github.pfichtner.pacto.matchers.Matchers.*;

import org.assertj.core.error.ShouldBeNumeric.NumericType;
import org.junit.jupiter.api.Test;

class MatchersTest {

	@Test
	void canCompile() {
		floatArg(decimalType());
		floatWrapperArg(decimalType());
		doubleArg(decimalType());
		// TODO fix to get rid of cast
		doubleWrapperArg((double) decimalType());
		
		intArg(integerType());
		integerWrapperArg(integerType());
		longArg(integerType());
		// TODO fix to get rid of cast
		longWrapperArg((long) integerType());
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

}
