package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.Pacto.delegate;
import static com.github.pfichtner.pacto.Pacto.invocations;
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.PactoDslBuilder.buildDslFrom;
import static com.github.pfichtner.pacto.matchers.Matchers.DEFAULT_INTEGER_VALUE;
import static com.github.pfichtner.pacto.matchers.Matchers.DEFAULT_STRING_VALUE;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.ValueSource;

import com.github.pfichtner.pacto.matchers.IntegerTypeArg;
import com.github.pfichtner.pacto.matchers.RegexArg;
import com.github.pfichtner.pacto.matchers.StringTypeArg;
import com.github.pfichtner.pacto.testdata.TestMother;
import com.github.pfichtner.pacto.testdata.chainedfluent.TestMotherChainedFluent;
import com.github.pfichtner.pacto.testdata.javabean.TestMotherJavaBean;
import com.google.gson.Gson;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

@ParameterizedClass
@ValueSource(classes = { TestMotherJavaBean.class, TestMotherChainedFluent.class })
public class PactoTest {

	private final Object dtoWithSpec;
	private final Object dto;
	private final Object partial;

	public PactoTest(Class<TestMother> clazz) throws Exception {
		TestMother testMother = clazz.getConstructor().newInstance();
		dto = testMother.dto();
		dtoWithSpec = testMother.dtoWithSpec();
		partial = testMother.partial();
	}

	@Test
	void testInvocations() {
		assertThat(invocations(dtoWithSpec).getAllInvocations())
				.extracting(i -> i.getAttribute(), i -> i.getMatcher() == null ? null : i.getMatcher().getClass())
				.containsExactly( //
						tuple("givenname", RegexArg.class), //
						tuple("lastname", RegexArg.class), //
						tuple("givenname", null), //
						tuple("lastname", StringTypeArg.class), //
						tuple("address", null), //
						tuple("age", IntegerTypeArg.class), //
						tuple("children", null) //
				);
	}

	@Test
	void testDelegate() throws Exception {
		assertThat(delegate(spec(dto))).isSameAs(dto);
	}

	@Test
	void doesSerializeLikeTheObjectItself() {
		Gson gson = new Gson();
		String serialized = gson.toJson(dtoWithSpec);

		assertThat(serialized).isEqualTo(gson.toJson(delegate(dtoWithSpec)));
		assertThatJson(serialized).node("givenname").isEqualTo("Givenname2");
		assertThatJson(serialized).node("lastname").isEqualTo("Lastname2");
		assertThatJson(serialized).node("age").isEqualTo(42);
		assertThatJson(serialized).node("address.zip").isEqualTo(DEFAULT_INTEGER_VALUE);
		assertThatJson(serialized).node("address.city").isEqualTo(DEFAULT_STRING_VALUE);
	}

	@Test
	void testDslPart() throws Exception {
		assertThat(buildDslFrom(dtoWithSpec).toString()).isEqualTo(dtoExpectedPactDslPart().toString());
	}

	@Test
	void partitial() throws Exception {
		Gson gson = new Gson();
		String serialized = gson.toJson(partial);

		assertThat(serialized).isEqualTo(gson.toJson(delegate(partial)));
		assertThatJson(serialized).node("givenname").isEqualTo("Givenname2");
		assertThatJson(serialized).node("lastname").isEqualTo("Lastname2");
		assertThatJson(serialized).node("age").isEqualTo(42);
		assertThatJson(serialized).node("address.zip").isEqualTo(12345);
		assertThatJson(serialized).node("address.city").isEqualTo("city");
		assertThatJson(serialized).node("address.country").isAbsent();
	}

	@Test
	void testDslPartWithPartitials() throws Exception {
		String expected = new PactDslJsonBody().stringType("lastname", "Lastname2").toString();
		assertThat(buildDslFrom(partial).toString()).isEqualTo(expected);
	}

	/**
	 * This is the way you would define the pact using pact-dsl. This pact should be
	 * the result of {@link TestMother#dtoWithSpec()}
	 * 
	 * @return the pact-dsl
	 */
	private static DslPart dtoExpectedPactDslPart() {
		return new PactDslJsonBody() //
				.stringMatcher("givenname", "G.*", "Givenname2") //
				.stringType("lastname", "Lastname2") //
				.integerType("age", 42) //
				.numberType("children", 2) //
				.object("address") //
				.integerType("zip", DEFAULT_INTEGER_VALUE) //
				.stringType("city", DEFAULT_STRING_VALUE) //
				.nullValue("country") //
				.closeObject() //
		;
	}
}
