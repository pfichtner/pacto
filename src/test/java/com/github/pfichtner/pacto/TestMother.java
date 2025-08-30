package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.DTOPactContract.contractFor;
import static com.github.pfichtner.pacto.PactMatchers.integerType;
import static com.github.pfichtner.pacto.PactMatchers.regex;
import static com.github.pfichtner.pacto.PactMatchers.stringType;

public class TestMother {

	static Object dataDto() {
		DataDto dto = contractFor(new DataDto());
		dto.setGivenname(regex("G.*", "Givenname1"));
		dto.setLastname(regex("L.*", "Lastname1"));
		dto.setGivenname("Givenname2"); // last one wins
		dto.setLastname(stringType("Lastname2")); // last one wins
		dto.setAge(integerType(42));
		// TODO support like
		dto.setAddress(contractFor(new AddressDTO()).zip(integerType()).city(stringType()));
		return dto;
	}

	static Object chainedFluentDto() {
		return contractFor(new ChainedAndFluent()) //
				.givenname(regex("G.*", "Givenname1")) //
				.lastname(regex("L.*", "Lastname1")) //
				.givenname("Givenname2") // last one wins
				.lastname(stringType("Lastname2")) // last one wins
				.age(integerType(42)) //
				// TODO support like
				.address(contractFor(new AddressDTO()).zip(integerType()).city(stringType()));
	}

}
