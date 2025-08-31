package com.github.pfichtner.pacto.testdata.javabean;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class PersonDTO {

	String givenname;
	String lastname;
	AddressDTO primaryAddress;
	AddressDTO[] secondaryAddresses;
	List<AddressDTO> secondaryAddressesList;
	Set<AddressDTO> secondaryAddressesSet;
	int age;
	double height;
	Double shoeSize;
	int children;

}