package com.github.pfichtner.pacto.testdata;

import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.Matchers.integerType;
import static com.github.pfichtner.pacto.matchers.Matchers.regex;
import static com.github.pfichtner.pacto.matchers.Matchers.stringType;

public class TestMother {

	public static Object javaBeanDto() {
		com.github.pfichtner.pacto.testdata.javabeans.PersonDTO dto = spec(
				new com.github.pfichtner.pacto.testdata.javabeans.PersonDTO());
		dto.setGivenname(regex("G.*", "Givenname1"));
		dto.setLastname(regex("L.*", "Lastname1"));
		dto.setGivenname("Givenname2"); // last one wins
		dto.setLastname(stringType("Lastname2")); // last one wins
		dto.setAge(integerType(42));
		// TODO support like
		dto.setAddress(javaBeanAddress());
		return dto;
	}

	private static com.github.pfichtner.pacto.testdata.javabeans.AddressDTO javaBeanAddress() {
		com.github.pfichtner.pacto.testdata.javabeans.AddressDTO address = spec(
				new com.github.pfichtner.pacto.testdata.javabeans.AddressDTO());
		address.setZip(integerType());
		address.setCity(stringType());
		return address;
	}

	public static Object chainedFluentDto() {
		return spec(new com.github.pfichtner.pacto.testdata.chainedfluent.PersonDTO()) //
				.givenname(regex("G.*", "Givenname1")) //
				.lastname(regex("L.*", "Lastname1")) //
				.givenname("Givenname2") // last one wins
				.lastname(stringType("Lastname2")) // last one wins
				.age(integerType(42)) //
				// TODO support like
				.address(spec(new com.github.pfichtner.pacto.testdata.chainedfluent.AddressDTO()).zip(integerType())
						.city(stringType()));
	}

}
