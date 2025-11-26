package com.github.pfichtner.pacto;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class DefaultInvocationTest {

	@Test
	void equalsContract() {
		assertDoesNotThrow(EqualsVerifier.forClass(DefaultInvocation.class)::verify);
	}

}
