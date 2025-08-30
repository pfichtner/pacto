package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.DTOPactContract.contractFor;
import static com.github.pfichtner.pacto.DTOPactContract.delegate;
import static com.github.pfichtner.pacto.DTOPactContract.invocations;
import static com.github.pfichtner.pacto.PactDslBuilderFromDTO.buildDslFrom;
import static com.github.pfichtner.pacto.PactMatchers.DEFAULT_INTEGER_VALUE;
import static com.github.pfichtner.pacto.PactMatchers.DEFAULT_STRING_VALUE;
import static com.github.pfichtner.pacto.TestMother.*;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.Test;

import com.github.pfichtner.pacto.matchers.IntegerTypeArg;
import com.github.pfichtner.pacto.matchers.RegexArg;
import com.github.pfichtner.pacto.matchers.StringTypeArg;
import com.google.gson.Gson;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

public class DtoPactTest {

	@Test
	void testInvocations() {
		Object dto = chainedFluentDto();
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
		Object emptyDto = new ChainedAndFluent();
		assertThat(delegate(contractFor(emptyDto))).isSameAs(emptyDto);
	}

	@Test
	void doesSerializeLikeTheObjectItself_() {
		Gson gson = new Gson();
		Object dto = dataDto();
		String serialized = gson.toJson(dto);

		assertThat(serialized).isEqualTo(gson.toJson(delegate(dto)));
		assertThatJson(serialized).node("givenname").isEqualTo("Givenname2");
		assertThatJson(serialized).node("lastname").isEqualTo("Lastname2");
		assertThatJson(serialized).node("age").isEqualTo(42);
		assertThatJson(serialized).node("address.zip").isEqualTo(DEFAULT_INTEGER_VALUE);
		assertThatJson(serialized).node("address.city").isEqualTo(DEFAULT_STRING_VALUE);
	}

	@Test
	void doesSerializeLikeTheObjectItself() {
		Gson gson = new Gson();
		Object dto = chainedFluentDto();
		String serialized = gson.toJson(dto);

		assertThat(serialized).isEqualTo(gson.toJson(delegate(dto)));
		assertThatJson(serialized).node("givenname").isEqualTo("Givenname2");
		assertThatJson(serialized).node("lastname").isEqualTo("Lastname2");
		assertThatJson(serialized).node("age").isEqualTo(42);
		assertThatJson(serialized).node("address.zip").isEqualTo(DEFAULT_INTEGER_VALUE);
		assertThatJson(serialized).node("address.city").isEqualTo(DEFAULT_STRING_VALUE);
	}

	@Test
	void testDslPart() throws Exception {
		assertThat(buildDslFrom(chainedFluentDto()).toString()).isEqualTo(expectedPactDslPart().toString());
	}

	/**
	 * This is the way you would define the pact using pact-dsl. This pact should be
	 * the result of {@link TestMother#chainedFluentDto()}
	 * 
	 * @return the pact-dsl
	 */
	private static DslPart expectedPactDslPart() {
		return new PactDslJsonBody() //
				.stringMatcher("givenname", "G.*", "Givenname2") //
				.stringType("lastname", "Lastname2") //
				.integerType("age", 42) //
				.object("address") //
				.integerType("zip", DEFAULT_INTEGER_VALUE) //
				.stringType("city", DEFAULT_STRING_VALUE) //
				.closeObject() //
		;
	}

}
