package com.github.pfichtner.pacto;

import static lombok.AccessLevel.PRIVATE;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
@Accessors(chain = true, fluent = true)
public class ChainedAndFluentAddress {

	int zip;
	String city;

}
