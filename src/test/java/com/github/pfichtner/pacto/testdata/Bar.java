package com.github.pfichtner.pacto.testdata;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
public class Bar {
	private String value;
}