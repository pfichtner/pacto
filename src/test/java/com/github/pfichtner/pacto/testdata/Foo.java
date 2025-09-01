package com.github.pfichtner.pacto.testdata;

import java.util.List;
import java.util.Set;

import lombok.Setter;

@Setter
public class Foo {
	Bar[] bars1;
	List<Bar> bars2;
	Set<Bar> bars3;
}