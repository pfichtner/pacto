package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.Pacto.invocations;

import java.util.List;
import java.util.function.BiFunction;

import org.mockito.ArgumentMatcher;

import com.github.pfichtner.pacto.matchers.IntegerTypeArg;
import com.github.pfichtner.pacto.matchers.PactoMatcher;
import com.github.pfichtner.pacto.matchers.RegexArg;
import com.github.pfichtner.pacto.matchers.StringTypeArg;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

public final class PactoDslBuilder {

	static abstract class Extractor<T extends PactoMatcher<?>>
			implements BiFunction<Invocation, PactDslJsonBody, PactDslJsonBody> {

		private final Class<T> clazz;

		public Extractor(Class<T> clazz) {
			this.clazz = clazz;
		}

		public boolean matches(Class<?> clazz) {
			return this.clazz == clazz;
		}

		@Override
		public final PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body) {
			return apply(invocation, body, clazz.cast(invocation.getMatcher()));
		}

		public abstract PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body, T matcher);

	}

	private final static List<Extractor<? extends PactoMatcher<String>>> extractors = List.of( //
			new Extractor<>(RegexArg.class) {
				@Override
				public PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body, RegexArg matcher) {
					return body.stringMatcher(invocation.getAttribute(), matcher.getRegex(), matcher.getValue());
				}
			}, //
			new Extractor<>(StringTypeArg.class) {
				@Override
				public PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body, StringTypeArg matcher) {
					return body.stringMatcher(invocation.getAttribute(), matcher.getValue());
				}
			}, //
			new Extractor<>(IntegerTypeArg.class) {
				@Override
				public PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body, IntegerTypeArg matcher) {
					return body.integerType(invocation.getAttribute(), matcher.getValue());
				}
			});

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

		return extractors.stream() //
				.filter(e -> e.matches(matcher.getClass())) //
				.findFirst() //
				.map(e -> e.apply(invocation, body)) //
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("Cannot handle %s (%s)", attribute, invocation)));
	}

}
