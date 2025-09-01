package com.github.pfichtner.pacto.testdata.chainedfluent;

import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.decimalType;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.eachLike;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.integerType;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.nullValue;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.regex;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.stringType;

import java.util.List;
import java.util.Set;

import com.github.pfichtner.pacto.matchers.PactoMatchers.Lists;
import com.github.pfichtner.pacto.matchers.PactoMatchers.Sets;
import com.github.pfichtner.pacto.testdata.TestMother;

public class TestMotherChainedFluent implements TestMother {

	@Override
	public PersonDTO dto() {
		return new PersonDTO();
	}

	@Override
	public Object dtoWithSpec() {
		return spec(dto()) //
				.givenname(regex("G.*", "Givenname1")) //
				.lastname(regex("L.*", "Lastname1")) //
				.givenname("Givenname2") // last one wins
				.lastname(stringType("Lastname2")) // last one wins
				// TODO support like
				.primaryAddress(spec(new AddressDTO()).zip(integerType(21)).city(stringType()).country(nullValue())) //
				.secondaryAddresses(
						eachLike(spec(new AddressDTO()).zip(integerType(22)).city(stringType()).country(nullValue()))) //
				.secondaryAddressesList(Lists
						.eachLike(spec(new AddressDTO()).zip(integerType(23)).city(stringType()).country(nullValue()))) //
				.secondaryAddressesSet(Sets
						.eachLike(spec(new AddressDTO()).zip(integerType(24)).city(stringType()).country(nullValue()))) //
				.age(integerType(42)) //
				.height(decimalType(1.86)) //
				.shoeSize((double) decimalType()) //
				.children(2) //
		;
	}

	@Override
	public Object partial() {
		return spec(dto() //
				.givenname("Givenname2") //
				.lastname("Lastname1") //
				.primaryAddress(new AddressDTO().zip(21).city("city").country(null)) //
				.secondaryAddresses(new AddressDTO[] { new AddressDTO().zip(22).city("city").country(null) }) //
				.secondaryAddressesList(List.of(new AddressDTO().zip(23).city("city").country(null))) //
				.secondaryAddressesSet(Set.of(new AddressDTO().zip(24).city("city").country(null))) //
				.age(42) //
				.height(1.86) //
				.children(2) //
		).lastname(stringType("Lastname2")) // last one wins
		;
	}

}
