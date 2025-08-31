package com.github.pfichtner.pacto.matchers;

import static com.github.pfichtner.pacto.matchers.Matchers.decimalType;
import static com.github.pfichtner.pacto.matchers.Matchers.integerType;

import org.junit.jupiter.api.Test;

class MatchersTest {

	@Test
	void canCompile() {
		floatArg(decimalType());
		floatArg(decimalType(1.23F));
		floatWrapperArg(decimalType());
		floatWrapperArg(decimalType(1.23F));
		doubleArg(decimalType());
		doubleArg(decimalType(1.23));
		// TODO fix to get rid of cast
		doubleWrapperArg((double) decimalType());
		doubleWrapperArg(decimalType(1.23));

		intArg(integerType());
		intArg(integerType(42));
		integerWrapperArg(integerType());
		integerWrapperArg(integerType(42));
		longArg(integerType());
		longArg(integerType(42L));
		// TODO fix to get rid of cast
		longWrapperArg((long) integerType());
		longWrapperArg(integerType(42L));
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
