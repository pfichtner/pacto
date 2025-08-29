package com.github.pfichtner.dact;

import static lombok.AccessLevel.PRIVATE;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
@Accessors(chain = true, fluent = true)
public class MyDTO {

	String givenname;
	String lastname;
	int age;
	AddressDTO address;

}