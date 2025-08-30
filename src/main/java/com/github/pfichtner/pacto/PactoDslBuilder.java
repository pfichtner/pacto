package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.Pacto.invocations;

import org.mockito.ArgumentMatcher;

import com.github.pfichtner.pacto.matchers.IntegerTypeArg;
import com.github.pfichtner.pacto.matchers.RegexArg;
import com.github.pfichtner.pacto.matchers.StringTypeArg;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

public final class PactoDslBuilder {

	private PactoDslBuilder() {
		super();
	}

	public static DslPart buildDslFrom(Object dto) {
		return appendInvocations(new PactDslJsonBody(), dto);
	}

	private static PactDslJsonBody appendInvocations(PactDslJsonBody body, Object object) {
		// TODO double the loop, first add all attributes, then all sibling ones (to fix
		// the cast problem below)
		for (Invocation invocation : invocations(object).getAllInvocations()) {
			body = append(body, invocation);
		}
		return body;
	}

	private static PactDslJsonBody append(PactDslJsonBody body, Invocation invocation) {
		String attribute = invocation.attribute();

		ArgumentMatcher<?> matcher = invocation.getMatcher();
		if (matcher == null) {
			Class<?> parameter = invocation.type();
			if (CharSequence.class.isAssignableFrom(parameter)) {
				return body.stringMatcher(attribute, invocation.getArg().toString());
			}
			// TODO fix cast
			return (PactDslJsonBody) appendInvocations(body.object(attribute), invocation.getArg()).closeObject();
		}

		if (matcher instanceof RegexArg) {
			RegexArg regexArg = (RegexArg) matcher;
			return body.stringMatcher(attribute, regexArg.getRegex(), regexArg.getValue());
		}
		if (matcher instanceof StringTypeArg) {
			StringTypeArg stringTypeArg = (StringTypeArg) matcher;
			return body.stringMatcher(attribute, stringTypeArg.getValue());
		}
		if (matcher instanceof IntegerTypeArg) {
			IntegerTypeArg integerTypeArg = (IntegerTypeArg) matcher;
			return body.integerType(attribute, integerTypeArg.getValue());
		}

		throw new IllegalArgumentException(String.format("Cannot handle %s (%s)", attribute, invocation));
	}

}
