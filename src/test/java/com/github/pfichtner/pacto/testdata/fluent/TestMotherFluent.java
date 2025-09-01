package com.github.pfichtner.pacto.testdata.fluent;

import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.Matchers.decimalType;
import static com.github.pfichtner.pacto.matchers.Matchers.eachLike;
import static com.github.pfichtner.pacto.matchers.Matchers.integerType;
import static com.github.pfichtner.pacto.matchers.Matchers.nullValue;
import static com.github.pfichtner.pacto.matchers.Matchers.regex;
import static com.github.pfichtner.pacto.matchers.Matchers.stringType;

import java.util.List;
import java.util.Set;

import com.github.pfichtner.pacto.matchers.Matchers.Lists;
import com.github.pfichtner.pacto.matchers.Matchers.Sets;
import com.github.pfichtner.pacto.testdata.TestMother;

public class TestMotherFluent implements TestMother {

	@Override
	public PersonDTO dto() {
		return new PersonDTO();
	}

	@Override
	public Object dtoWithSpec() {
		PersonDTO dto = spec(dto());
		dto.givenname(regex("G.*", "Givenname1"));
		dto.lastname(regex("L.*", "Lastname1"));
		dto.givenname("Givenname2"); // last one wins
		dto.lastname(stringType("Lastname2")); // last one wins
		// TODO support like
		dto.primaryAddress(address(21));
		dto.secondaryAddresses(eachLike(address(22)));
		dto.secondaryAddressesList(Lists.eachLike(address(23)));
		dto.secondaryAddressesSet(Sets.eachLike(address(24)));
		dto.age(integerType(42));
		dto.height(decimalType(1.86));
		dto.shoeSize((double) decimalType());
		dto.children(2);
		return dto;
	}

	@Override
	public Object partial() {
		PersonDTO dto = dto();
		dto.givenname("Givenname2");
		dto.lastname("Lastname1");
		dto.primaryAddress(partialAddress(21));
		dto.secondaryAddresses(new AddressDTO[] { partialAddress(22) });
		dto.secondaryAddressesList(List.of(partialAddress(23)));
		dto.secondaryAddressesSet(Set.of(partialAddress(24)));
		dto.age(42);
		dto.height(1.86);
		dto.children(2);
		PersonDTO spec = spec(dto);
		spec.lastname(stringType("Lastname2")); // last one wins
		return spec;
	}

	private static AddressDTO partialAddress(int zip) {
		AddressDTO address = new AddressDTO();
		address.zip(zip);
		address.city("city");
		address.country(null);
		return address;
	}

	private static AddressDTO address(int zip) {
		AddressDTO address = spec(new AddressDTO());
		address.zip(integerType(zip));
		address.city(stringType());
		address.country(nullValue());
		return address;
	}

}
