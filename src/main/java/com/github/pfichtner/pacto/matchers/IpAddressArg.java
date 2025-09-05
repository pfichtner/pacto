package com.github.pfichtner.pacto.matchers;

public class IpAddressArg extends PactoMatcher<String> {

	public IpAddressArg() {
		super("127.0.0.1");
	}

	@Override
	public String toString() {
		return "ipAddress";
	}

}
