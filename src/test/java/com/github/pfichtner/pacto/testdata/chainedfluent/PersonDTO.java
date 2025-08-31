package com.github.pfichtner.pacto.testdata.chainedfluent;

import static lombok.AccessLevel.PRIVATE;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
@Accessors(chain = true, fluent = true)
public class PersonDTO {

	String givenname;
	String lastname;
	AddressDTO address;
	int age;
	int children;

}