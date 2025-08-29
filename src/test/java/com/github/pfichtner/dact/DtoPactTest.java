package com.github.pfichtner.dact;

import static com.github.pfichtner.dact.DTOPactContract.contractFor;
import static com.github.pfichtner.dact.DTOPactContract.delegate;
import static com.github.pfichtner.dact.DTOPactContract.invocations;
import static com.github.pfichtner.dact.PactDslBuilderFromDTO.buildDslFrom;
import static com.github.pfichtner.dact.PactMatchers.integerType;
import static com.github.pfichtner.dact.PactMatchers.regex;
import static com.github.pfichtner.dact.PactMatchers.stringType;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.Test;

import com.github.pfichtner.dact.matchers.IntegerTypeArg;
import com.github.pfichtner.dact.matchers.RegexArg;
import com.github.pfichtner.dact.matchers.StringTypeArg;
import com.google.gson.Gson;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

public class DtoPactTest {

	@Test
	void testInvocations() {
		MyDTO dto = myDto();
		assertThat(invocations(dto).getAllInvocations())
				.extracting(i -> i.attribute(), i -> i.getMatcher() == null ? null : i.getMatcher().getClass())
				.containsExactly( //
						tuple("givenname", RegexArg.class), //
						tuple("lastname", RegexArg.class), //
						tuple("givenname", null), //
						tuple("lastname", StringTypeArg.class), //
						tuple("age", IntegerTypeArg.class), //
						tuple("address", null));
	}

	@Test
	void testDelegate() {
		MyDTO dto = new MyDTO();
		assertThat(delegate(contractFor(dto))).isSameAs(dto);
	}

	@Test
	void doesSerializeLikeTheObjectItself() {
		Gson gson = new Gson();
		MyDTO myDto = myDto();
		String serialized = gson.toJson(myDto);

		assertThat(serialized).isEqualTo(gson.toJson(delegate(myDto)));
		assertThatJson(serialized).node("givenname").isEqualTo("Givenname2");
		assertThatJson(serialized).node("lastname").isEqualTo("Lastname2");
		assertThatJson(serialized).node("age").isEqualTo(42);
		assertThatJson(serialized).node("address.zip").isEqualTo(12345);
		assertThatJson(serialized).node("address.city").isEqualTo("City");
	}

	@Test
	void testDslPart() throws Exception {
		assertThat(buildDslFrom(myDto()).toString()).isEqualTo(expectedPactDslPart().toString());
	}

	private MyDTO myDto() {
		// TODO real test bean methods (setLastname, ...)
		return contractFor(new MyDTO()).givenname(regex("G.*", "Givenname1")) //
				.lastname(regex("L.*", "Lastname1")) //
				.givenname("Givenname2") //
				.lastname(stringType("Lastname2")) //
				.age(integerType(42)) //
				// TODO support like
				.address(contractFor(new AddressDTO()).zip(integerType(12345)).city(stringType("City")));
	}

	private DslPart expectedPactDslPart() {
		return new PactDslJsonBody() //
				.stringMatcher("givenname", "G.*", "Givenname2") //
				.stringType("lastname", "Lastname2") //
				.integerType("age", 42) //
				.object("address") //
				.integerType("zip", 12345) //
				.stringType("city", "City") //
				.closeObject() //
		;
	}

}
