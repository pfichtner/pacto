package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.DTOPactContract.contractFor;
import static com.github.pfichtner.pacto.PactMatchers.integerType;
import static com.github.pfichtner.pacto.PactMatchers.regex;
import static com.github.pfichtner.pacto.PactMatchers.stringType;

public class TestMother {

	static Object dataDto() {
		JavaBeanPerson dto = contractFor(new JavaBeanPerson());
		dto.setGivenname(regex("G.*", "Givenname1"));
		dto.setLastname(regex("L.*", "Lastname1"));
		dto.setGivenname("Givenname2"); // last one wins
		dto.setLastname(stringType("Lastname2")); // last one wins
		dto.setAge(integerType(42));
		// TODO support like
		dto.setAddress(javaBeanAddress());
		return dto;
	}

	private static JavaBeanAddress javaBeanAddress() {
		JavaBeanAddress address = contractFor(new JavaBeanAddress());
		address.setZip(integerType());
		address.setCity(stringType());
		return address;
	}

	static Object chainedFluentDto() {
		return contractFor(new ChainedAndFluentPerson()) //
				.givenname(regex("G.*", "Givenname1")) //
				.lastname(regex("L.*", "Lastname1")) //
				.givenname("Givenname2") // last one wins
				.lastname(stringType("Lastname2")) // last one wins
				.age(integerType(42)) //
				// TODO support like
				.address(contractFor(new ChainedAndFluentAddress()).zip(integerType()).city(stringType()));
	}

}
