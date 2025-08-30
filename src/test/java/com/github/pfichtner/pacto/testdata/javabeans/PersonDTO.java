package com.github.pfichtner.pacto.testdata.javabeans;

import static lombok.AccessLevel.PRIVATE;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class PersonDTO {

	String givenname;
	String lastname;
	int age;
	AddressDTO address;

}