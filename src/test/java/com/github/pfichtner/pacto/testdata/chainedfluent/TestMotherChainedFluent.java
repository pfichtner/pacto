package com.github.pfichtner.pacto.testdata.chainedfluent;

import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.Matchers.integerType;
import static com.github.pfichtner.pacto.matchers.Matchers.regex;
import static com.github.pfichtner.pacto.matchers.Matchers.stringType;

public class TestMotherChainedFluent {

	public static PersonDTO blank() {
		return new PersonDTO();
	}

	public static Object withSpec() {
		return spec(blank()) //
				.givenname(regex("G.*", "Givenname1")) //
				.lastname(regex("L.*", "Lastname1")) //
				.givenname("Givenname2") // last one wins
				.lastname(stringType("Lastname2")) // last one wins
				// TODO support like
				.address(spec(new AddressDTO()).zip(integerType()).city(stringType())).age(integerType(42)) //
		;
	}

}
