package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.DslPartAssert.assertThatDslPart;
import static com.github.pfichtner.pacto.Pacto.delegate;
import static com.github.pfichtner.pacto.Pacto.invocations;
import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.PactoDslBuilder.buildDslFrom;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.DEFAULT_DECIMAL_VALUE;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.DEFAULT_STRING_VALUE;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.ValueSource;

import com.github.pfichtner.pacto.matchers.DecimalTypeArg;
import com.github.pfichtner.pacto.matchers.EachLikeArg;
import com.github.pfichtner.pacto.matchers.IntegerTypeArg;
import com.github.pfichtner.pacto.matchers.NumberTypeArg;
import com.github.pfichtner.pacto.matchers.StringMatcherArg;
import com.github.pfichtner.pacto.matchers.StringTypeArg;
import com.github.pfichtner.pacto.testdata.TestMother;
import com.github.pfichtner.pacto.testdata.chained.TestMotherChained;
import com.github.pfichtner.pacto.testdata.chainedfluent.TestMotherChainedFluent;
import com.github.pfichtner.pacto.testdata.fluent.TestMotherFluent;
import com.github.pfichtner.pacto.testdata.javabean.TestMotherJavaBean;
import com.google.gson.Gson;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

@ParameterizedClass
@ValueSource(classes = { TestMotherJavaBean.class, TestMotherChained.class, TestMotherFluent.class,
		TestMotherChainedFluent.class })
public class PactoTest {

	private final Object dtoWithSpec;
	private final Object dto;
	private final Object partial;

	private final Gson gson = new Gson();

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
						tuple("givenname", StringMatcherArg.class), //
						tuple("lastname", StringMatcherArg.class), //
						tuple("givenname", null), //
						tuple("lastname", StringTypeArg.class), //
						tuple("primaryAddress", null), //
						tuple("secondaryAddresses", EachLikeArg.class), //
						tuple("secondaryAddressesList", EachLikeArg.class), //
						tuple("secondaryAddressesSet", EachLikeArg.class), //
						tuple("age", IntegerTypeArg.class), //
						tuple("height", DecimalTypeArg.class), //
						tuple("shoeSize", DecimalTypeArg.class), //
						tuple("children", null), //
						tuple("salary", NumberTypeArg.class) //
				);
	}

	@Test
	void testDelegate() throws Exception {
		assertThat(delegate(spec(dto))).isSameAs(dto);
	}

	@Test
	void doesSerializeLikeTheObjectItself() {
		String expected = """
				{
					"givenname":"Givenname2",
					"lastname":"Lastname2",
					"primaryAddress":{
						"zip":21,
						"city":"string"
					},
					"secondaryAddresses":[
						{"zip":22,"city":"string"}
					],
					"secondaryAddressesList":[
						{"zip":23,"city":"string"}
					],
					"secondaryAddressesSet":[
						{"zip":24,"city":"string"}
					],
					"age":42,
					"height":1.86,
					"shoeSize":12.345000267028809,
					"children":2,
					"salary":123
				}
				""";

		String serialized = gson.toJson(dtoWithSpec);
		assertThatJson(serialized).isEqualTo(expected);
		assertThat(serialized).isEqualTo(gson.toJson(delegate(dtoWithSpec)));
	}

	@Test
	void testDslPart() throws Exception {
		assertThatDslPart(buildDslFrom(dtoWithSpec)).isEqualToDslPart(dtoExpectedPactDslPart());
	}

	@Test
	void partitial() throws Exception {
		String expected = """
				{
					"givenname":"Givenname2",
					"lastname":"Lastname2",
					"primaryAddress":{
						"zip":21,
						"city":"city"
					},
					"secondaryAddresses":[
						{"zip":22,"city":"city"}
					],
					"secondaryAddressesList":[
						{"zip":23,"city":"city"}
					],
					"secondaryAddressesSet":[
						{"zip":24,"city":"city"}
					],
					"age":42,
					"height":1.86,
					"children":2
				}
				""";

		String serialized = gson.toJson(partial);
		assertThatJson(serialized).isEqualTo(expected);
	}

	@Test
	void testDslPartWithPartitials() throws Exception {
		DslPart expected = new PactDslJsonBody().stringType("lastname", "Lastname2");
		assertThatDslPart(buildDslFrom(partial)).isEqualToDslPart(expected);
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
				.stringMatcher("lastname", "L.*", "Lastname2") //
				.stringType("lastname", "Lastname2") //
				.integerType("age", 42) //
				.decimalType("height", 1.86) //
				.decimalType("shoeSize", (double) DEFAULT_DECIMAL_VALUE) //
				.integerType("children").numberValue("children", 2) //
				.numberType("salary", 123) //
				.object("primaryAddress") //
				.integerType("zip", 21) //
				.stringType("city", DEFAULT_STRING_VALUE) //
				.nullValue("country") //
				.closeObject() //
				.eachLike("secondaryAddresses", inner(22)) //
				.eachLike("secondaryAddressesList", inner(23)) //
				.eachLike("secondaryAddressesSet", inner(24)) //
		;
	}

	private static PactDslJsonBody inner(int zip) {
		return new PactDslJsonBody() //
				.integerType("zip", zip) //
				.stringType("city", DEFAULT_STRING_VALUE) //
				.nullValue("country") //
		;
	}
}
