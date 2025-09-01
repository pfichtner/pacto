package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.Pacto.invocations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.mockito.ArgumentMatcher;

import com.github.pfichtner.pacto.matchers.DecimalTypeArg;
import com.github.pfichtner.pacto.matchers.EachLikeArg;
import com.github.pfichtner.pacto.matchers.IntegerTypeArg;
import com.github.pfichtner.pacto.matchers.NullValueArg;
import com.github.pfichtner.pacto.matchers.NumberTypeArg;
import com.github.pfichtner.pacto.matchers.PactoMatcher;
import com.github.pfichtner.pacto.matchers.StringMatcherArg;
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
			new Extractor<>(StringMatcherArg.class) {
				@Override
				public PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body, StringMatcherArg matcher) {
					return body.stringMatcher(invocation.getAttribute(), matcher.getRegex(), matcher.getValue());
				}
			}, //
			new Extractor<>(StringTypeArg.class) {
				@Override
				public PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body, StringTypeArg matcher) {
					return body.stringType(invocation.getAttribute(), matcher.getValue());
				}
			}, //
			new Extractor<>(IntegerTypeArg.class) {
				@Override
				public PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body, IntegerTypeArg matcher) {
					return body.integerType(invocation.getAttribute(), matcher.getValue());
				}
			}, //
			new Extractor<>(DecimalTypeArg.class) {
				@Override
				public PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body, DecimalTypeArg matcher) {
					return body.decimalType(invocation.getAttribute(), matcher.getValue());
				}
			}, //
			new Extractor<>(NumberTypeArg.class) {
				@Override
				public PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body, NumberTypeArg matcher) {
					return body.numberType(invocation.getAttribute(), matcher.getValue());
				}
			}, //
			new Extractor<>(EachLikeArg.class) {
				@Override
				public PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body, EachLikeArg matcher) {
					Integer max = matcher.getMax();
					Integer min = matcher.getMin();
					DslPart each = buildDslFrom(matcher.getValue());
					if (max != null) {
						return body.maxArrayLike(invocation.getAttribute(), max, each);
					}
					if (min != null) {
						return body.minArrayLike(invocation.getAttribute(), min, each);
					}
					return body.eachLike(invocation.getAttribute(), each);
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
				return body.stringValue(attribute, invocation.getArg().toString());
			} else if (int.class.isAssignableFrom(parameter)) {
				return body.numberType(attribute).numberValue(attribute, (int) invocation.getArg());
			} else if (long.class.isAssignableFrom(parameter)) {
				return body.numberType(attribute).numberValue(attribute, (long) invocation.getArg());
			} else if (double.class.isAssignableFrom(parameter)) {
				return body.numberType(attribute).numberValue(attribute, (double) invocation.getArg());
			} else if (float.class.isAssignableFrom(parameter)) {
				return body.numberType(attribute).numberValue(attribute, (float) invocation.getArg());
			} else if (boolean.class.isAssignableFrom(parameter) || Boolean.class.isAssignableFrom(parameter)) {
				return body.booleanType(attribute).booleanValue(attribute, (boolean) invocation.getArg());
			} else if (Number.class.isAssignableFrom(parameter)) {
				return body.numberType(attribute).numberValue(attribute, (Number) invocation.getArg());
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
