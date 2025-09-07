package com.github.pfichtner.pacto.testdata.fluent;

import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.github.pfichtner.pacto.matchers.PactoMatchers.Lists;
import com.github.pfichtner.pacto.matchers.PactoMatchers.Sets;
import com.github.pfichtner.pacto.testdata.TestMother;

public class TestMotherFluent implements TestMother {

	@Override
	public PersonDTO dto() {
		return new PersonDTO();
	}

	@Override
	public Object dtoWithSpec() {
		PersonDTO dto = spec(dto());
		dto.givenname(stringMatcher("G.*", "Givenname1"));
		dto.lastname(stringMatcher("L.*", "Lastname1"));
		dto.givenname("Givenname2");
		dto.lastname(stringType("Lastname2"));
		AddressDTO address1 = address();
		address1.zip(integerType(21));
		address1.validated(true);
		dto.primaryAddress(address1);
		AddressDTO address2 = address();
		address2.zip(integerType(22));
		address2.validated(false);
		dto.secondaryAddresses(eachLike(address2));
		AddressDTO address3 = address();
		address3.zip(integerType(23));
		address3.validated(booleanType(true));
		dto.secondaryAddressesList(Lists.eachLike(address3));
		AddressDTO address4 = address();
		address4.zip(integerType(24));
		address4.validated(booleanValue(true));
		dto.secondaryAddressesSet(Sets.eachLike(address4));
		dto.age(integerType(42));
		dto.height(decimalType(1.86));
		dto.shoeSize((double) decimalType());
		dto.children(2);
		dto.salary(numberType(new BigDecimal(123)));
		return dto;
	}

	private AddressDTO address() {
		AddressDTO address4 = spec(new AddressDTO());
		address4.city(stringType());
		address4.country(nullValue());
		return address4;
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
		spec.lastname(stringType("Lastname2"));
		return spec;
	}

	private static AddressDTO partialAddress(int zip) {
		AddressDTO address = new AddressDTO();
		address.zip(zip);
		address.city("city");
		address.country(null);
		address.validated(true);
		return address;
	}

}
