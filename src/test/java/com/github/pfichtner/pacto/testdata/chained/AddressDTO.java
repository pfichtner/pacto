package com.github.pfichtner.pacto.testdata.chained;

import static lombok.AccessLevel.PRIVATE;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
@Accessors(chain = true, fluent = false)
public class AddressDTO {

	int zip;
	String city;
	String country;
	boolean validated;

}
