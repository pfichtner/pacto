package com.github.pfichtner.pacto.testdata.javabean;

import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.Matchers.integerType;
import static com.github.pfichtner.pacto.matchers.Matchers.regex;
import static com.github.pfichtner.pacto.matchers.Matchers.stringType;

public class TestMotherJavaBean {

	public static PersonDTO dto() {
		return new PersonDTO();
	}

	public static Object dtoWithSpec() {
		PersonDTO dto = spec(dto());
		dto.setGivenname(regex("G.*", "Givenname1"));
		dto.setLastname(regex("L.*", "Lastname1"));
		dto.setGivenname("Givenname2"); // last one wins
		dto.setLastname(stringType("Lastname2")); // last one wins
		// TODO support like
		dto.setAddress(address());
		dto.setAge(integerType(42));
		return dto;
	}

	private static AddressDTO address() {
		AddressDTO address = spec(new com.github.pfichtner.pacto.testdata.javabean.AddressDTO());
		address.setZip(integerType());
		address.setCity(stringType());
		return address;
	}

}
