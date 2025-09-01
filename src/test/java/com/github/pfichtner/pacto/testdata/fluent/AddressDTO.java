package com.github.pfichtner.pacto.testdata.fluent;

import static lombok.AccessLevel.PRIVATE;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
@Accessors(chain = false, fluent = true)
public class AddressDTO {

	int zip;
	String city;
	String country;

}
