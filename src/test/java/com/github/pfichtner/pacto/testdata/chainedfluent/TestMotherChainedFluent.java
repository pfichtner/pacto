package com.github.pfichtner.pacto.testdata.chainedfluent;

import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.Matchers.integerType;
import static com.github.pfichtner.pacto.matchers.Matchers.nullValue;
import static com.github.pfichtner.pacto.matchers.Matchers.regex;
import static com.github.pfichtner.pacto.matchers.Matchers.stringType;

public class TestMotherChainedFluent {

	public static PersonDTO dto() {
		return new PersonDTO();
	}

	public static Object dtoWithSpec() {
		return spec(dto()) //
				.givenname(regex("G.*", "Givenname1")) //
				.lastname(regex("L.*", "Lastname1")) //
				.givenname("Givenname2") // last one wins
				.lastname(stringType("Lastname2")) // last one wins
				// TODO support like
				.address(spec(new AddressDTO()).zip(integerType()).city(stringType()).country(nullValue())) //
				.age(integerType(42)) //
				.children(2) //
		;
	}

	public static Object partial() {
		return spec(dto().givenname("Givenname2") //
				.lastname("Lastname1") //
				.address(new AddressDTO().zip(12345).city("city").country(null)) //
				.age(42) //
				.children(2) //
		).lastname(stringType("Lastname2")) // last one wins
		;
	}

}
