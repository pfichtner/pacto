package com.github.pfichtner.pacto;

import static lombok.AccessLevel.PRIVATE;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class JavaBeanAddress {

	int zip;
	String city;

}