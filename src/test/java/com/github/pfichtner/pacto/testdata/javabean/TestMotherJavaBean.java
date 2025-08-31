package com.github.pfichtner.pacto.testdata.javabean;

import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.Matchers.integerType;
import static com.github.pfichtner.pacto.matchers.Matchers.nullValue;
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
		dto.setChildren(2);
		return dto;
	}

	public static Object partial() {
		PersonDTO dto = dto();
		dto.setGivenname("Givenname2");
		dto.setLastname("Lastname1");
		dto.setAddress(partialAddress());
		dto.setAge(42);
		dto.setChildren(2);
		PersonDTO spec = spec(dto);
		spec.setLastname(stringType("Lastname2")); // last one wins
		return spec;
	}

	private static AddressDTO partialAddress() {
		AddressDTO address = new AddressDTO();
		address.setZip(12345);
		address.setCity("city");
		address.setCountry(null);
		return address;
	}

	private static AddressDTO address() {
		AddressDTO address = spec(new AddressDTO());
		address.setZip(integerType());
		address.setCity(stringType());
		address.setCountry(nullValue());
		return address;
	}

}
