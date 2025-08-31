package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.Pacto.invocations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.mockito.ArgumentMatcher;

import com.github.pfichtner.pacto.matchers.IntegerTypeArg;
import com.github.pfichtner.pacto.matchers.NullValueArg;
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

		public boolean matches(ArgumentMatcher<?> matcher) {
			return this.clazz.isInstance(matcher);
		}

		@Override
		public final PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body) {
			return apply(invocation, body, clazz.cast(invocation.getMatcher()));
		}

		public abstract PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body, T matcher);

	}

	private final static List<Extractor<? extends PactoMatcher<?>>> extractors = List.of( //
			new Extractor<>(NullValueArg.class) {
				@Override
				public PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body, NullValueArg matcher) {
					return body.nullValue(invocation.getAttribute());
				}
			}, //
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

	private static DslPart appendInvocations(PactDslJsonBody body, Object object) {
		return appendInvocations(body, invocations(object).getAllInvocations());
	}

	protected static DslPart appendInvocations(PactDslJsonBody body, List<Invocation> invocations) {
		List<Invocation> pushbackInvocations = new ArrayList<>();
		for (Invocation invocation : invocations) {
			body = append(body, invocation, pushbackInvocations);
		}

		DslPart bodyWithNested = body;
		for (Invocation invocation : pushbackInvocations) {
			bodyWithNested = appendInvocations(bodyWithNested.object(invocation.getAttribute()), invocation.getArg())
					.closeObject();
		}
		return bodyWithNested;
	}

	private static PactDslJsonBody append(PactDslJsonBody body, Invocation invocation,
			List<Invocation> pushbackInvocations) {
		String attribute = invocation.getAttribute();
		ArgumentMatcher<?> matcher = invocation.getMatcher();
		if (matcher == null) {
			Class<?> parameter = invocation.getType();
			if (CharSequence.class.isAssignableFrom(parameter)) {
				return body.stringMatcher(attribute, invocation.getArg().toString());
			} else if (int.class.isAssignableFrom(parameter)) {
				return body.numberValue(attribute, (int) invocation.getArg());
			} else if (long.class.isAssignableFrom(parameter)) {
				return body.numberValue(attribute, (long) invocation.getArg());
			} else if (double.class.isAssignableFrom(parameter)) {
				return body.numberValue(attribute, (double) invocation.getArg());
			} else if (float.class.isAssignableFrom(parameter)) {
				return body.numberValue(attribute, (float) invocation.getArg());
			} else if (boolean.class.isAssignableFrom(parameter) || Boolean.class.isAssignableFrom(parameter)) {
				return body.booleanValue(attribute, (boolean) invocation.getArg());
			} else if (Number.class.isAssignableFrom(parameter)) {
				return body.numberValue(attribute, (Number) invocation.getArg());
			}
			pushbackInvocations.add(invocation);
			return body;
		}

		return extractors.stream() //
				.filter(e -> e.matches(matcher)) //
				.findFirst() //
				.map(e -> e.apply(invocation, body)) //
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("Cannot handle %s (%s)", attribute, invocation)));
	}

}
