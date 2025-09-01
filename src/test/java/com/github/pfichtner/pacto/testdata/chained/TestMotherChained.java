package com.github.pfichtner.pacto.testdata.chained;

import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.decimalType;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.eachLike;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.integerType;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.nullValue;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.numberType;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.stringMatcher;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.stringType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.github.pfichtner.pacto.matchers.PactoMatchers.Lists;
import com.github.pfichtner.pacto.matchers.PactoMatchers.Sets;
import com.github.pfichtner.pacto.testdata.TestMother;

public class TestMotherChained implements TestMother {

	@Override
	public PersonDTO dto() {
		return new PersonDTO();
	}

	@Override
	public Object dtoWithSpec() {
		return spec(dto()) //
				.setGivenname(stringMatcher("G.*", "Givenname1")) //
				.setLastname(stringMatcher("L.*", "Lastname1")) //
				.setGivenname("Givenname2") // last one wins
				.setLastname(stringType("Lastname2")) // last one wins
				// TODO support like
				.setPrimaryAddress(
						spec(new AddressDTO()).setZip(integerType(21)).setCity(stringType()).setCountry(nullValue())) //
				.setSecondaryAddresses(eachLike(
						spec(new AddressDTO()).setZip(integerType(22)).setCity(stringType()).setCountry(nullValue()))) //
				.setSecondaryAddressesList(Lists.eachLike(
						spec(new AddressDTO()).setZip(integerType(23)).setCity(stringType()).setCountry(nullValue()))) //
				.setSecondaryAddressesSet(Sets.eachLike(
						spec(new AddressDTO()).setZip(integerType(24)).setCity(stringType()).setCountry(nullValue()))) //
				.setAge(integerType(42)) //
				.setHeight(decimalType(1.86)) //
				.setShoeSize((double) decimalType()) //
				.setChildren(2) //
				.setSalary(numberType(new BigDecimal(123))) //
		;
	}

	@Override
	public Object partial() {
		return spec(dto() //
				.setGivenname("Givenname2") //
				.setLastname("Lastname1") //
				.setPrimaryAddress(new AddressDTO().setZip(21).setCity("city").setCountry(null)) //
				.setSecondaryAddresses(
						new AddressDTO[] { new AddressDTO().setZip(22).setCity("city").setCountry(null) }) //
				.setSecondaryAddressesList(List.of(new AddressDTO().setZip(23).setCity("city").setCountry(null))) //
				.setSecondaryAddressesSet(Set.of(new AddressDTO().setZip(24).setCity("city").setCountry(null))) //
				.setAge(42) //
				.setHeight(1.86) //
				.setChildren(2) //
		).setLastname(stringType("Lastname2")) // last one wins
		;
	}

}
