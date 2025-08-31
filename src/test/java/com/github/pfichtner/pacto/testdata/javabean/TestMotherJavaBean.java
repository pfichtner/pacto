package com.github.pfichtner.pacto.testdata.javabean;

import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.Matchers.*;

import java.util.List;
import java.util.Set;

import com.github.pfichtner.pacto.matchers.Matchers.Lists;
import com.github.pfichtner.pacto.matchers.Matchers.Sets;
import com.github.pfichtner.pacto.testdata.TestMother;

public class TestMotherJavaBean implements TestMother {

	@Override
	public PersonDTO dto() {
		return new PersonDTO();
	}

	@Override
	public Object dtoWithSpec() {
		PersonDTO dto = spec(dto());
		dto.setGivenname(regex("G.*", "Givenname1"));
		dto.setLastname(regex("L.*", "Lastname1"));
		dto.setGivenname("Givenname2"); // last one wins
		dto.setLastname(stringType("Lastname2")); // last one wins
		// TODO support like
		dto.setPrimaryAddress(address(21));
		dto.setSecondaryAddresses(eachLike(address(22)));
		dto.setSecondaryAddressesList(Lists.eachLike(address(23)));
		dto.setSecondaryAddressesSet(Sets.eachLike(address(24)));
		dto.setAge(integerType(42));
		dto.setHeight(decimalType(1.86));
		dto.setShoeSize(decimalType());
		dto.setChildren(2);
		return dto;
	}

	@Override
	public Object partial() {
		PersonDTO dto = dto();
		dto.setGivenname("Givenname2");
		dto.setLastname("Lastname1");
		dto.setPrimaryAddress(partialAddress(21));
		dto.setSecondaryAddresses(new AddressDTO[] { partialAddress(22) });
		dto.setSecondaryAddressesList(List.of(partialAddress(23)));
		dto.setSecondaryAddressesSet(Set.of(partialAddress(24)));
		dto.setAge(42);
		dto.setHeight(1.86);
		dto.setChildren(2);
		PersonDTO spec = spec(dto);
		spec.setLastname(stringType("Lastname2")); // last one wins
		return spec;
	}

	private static AddressDTO partialAddress(int zip) {
		AddressDTO address = new AddressDTO();
		address.setZip(zip);
		address.setCity("city");
		address.setCountry(null);
		return address;
	}

	private static AddressDTO address(int zip) {
		AddressDTO address = spec(new AddressDTO());
		address.setZip(integerType(zip));
		address.setCity(stringType());
		address.setCountry(nullValue());
		return address;
	}

}
