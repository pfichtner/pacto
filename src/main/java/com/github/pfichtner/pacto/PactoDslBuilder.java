package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.Pacto.invocations;

import java.util.Map;
import java.util.function.BiFunction;

import org.mockito.ArgumentMatcher;

import com.github.pfichtner.pacto.matchers.IntegerTypeArg;
import com.github.pfichtner.pacto.matchers.RegexArg;
import com.github.pfichtner.pacto.matchers.StringTypeArg;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

public final class PactoDslBuilder {

	private static final Map<Class<? extends ArgumentMatcher<?>>, BiFunction<Invocation, PactDslJsonBody, PactDslJsonBody>> converters //
			= Map.of( //
					RegexArg.class, (i, b) -> {
						RegexArg regexArg = (RegexArg) i.getMatcher();
						return b.stringMatcher(i.getAttribute(), regexArg.getRegex(), regexArg.getValue());
					}, //
					StringTypeArg.class, (i, b) -> {
						StringTypeArg stringTypeArg = (StringTypeArg) i.getMatcher();
						return b.stringMatcher(i.getAttribute(), stringTypeArg.getValue());
					}, //
					IntegerTypeArg.class, (i, b) -> {
						IntegerTypeArg integerTypeArg = (IntegerTypeArg) i.getMatcher();
						return b.integerType(i.getAttribute(), integerTypeArg.getValue());
					} //
			);

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
		String attribute = invocation.getAttribute();
		ArgumentMatcher<?> matcher = invocation.getMatcher();
		if (matcher == null) {
			Class<?> parameter = invocation.getType();
			if (CharSequence.class.isAssignableFrom(parameter)) {
				return body.stringMatcher(attribute, invocation.getArg().toString());
			}
			// TODO fix cast
			return (PactDslJsonBody) appendInvocations(body.object(attribute), invocation.getArg()).closeObject();
		}

		BiFunction<Invocation, PactDslJsonBody, PactDslJsonBody> function = converters.get(matcher.getClass());
		if (function == null) {
			throw new IllegalArgumentException(String.format("Cannot handle %s (%s)", attribute, invocation));
		}
		return function.apply(invocation, body);
	}

}
