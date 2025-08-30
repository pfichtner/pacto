package com.github.pfichtner.pacto;

import static lombok.AccessLevel.PRIVATE;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class DataDto {

	String givenname;
	String lastname;
	int age;
	AddressDTO address;

}