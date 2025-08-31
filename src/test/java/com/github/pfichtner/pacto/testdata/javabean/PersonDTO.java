package com.github.pfichtner.pacto.testdata.javabean;

import static lombok.AccessLevel.PRIVATE;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class PersonDTO {

	String givenname;
	String lastname;
	AddressDTO primaryAddress;
	AddressDTO[] secondaryAddresses;
	int age;
	int children;

}