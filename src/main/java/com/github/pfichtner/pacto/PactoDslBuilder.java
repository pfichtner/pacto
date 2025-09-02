package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.Pacto.invocations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.mockito.ArgumentMatcher;

import com.github.pfichtner.pacto.matchers.BooleanTypeArg;
import com.github.pfichtner.pacto.matchers.BooleanValueArg;
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

	private static <T extends PactoMatcher<?>> Extractor<T> x(Class<T> clazz, Extractor.Applier<T> applier) {
		return new Extractor<T>(clazz, applier);
	}

	private static class Extractor<T extends PactoMatcher<?>>
			implements BiFunction<Invocation, PactDslJsonBody, PactDslJsonBody> {

		static interface Applier<T> {
			PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body, T matcher);
		}

		private final Class<T> clazz;
		private final Applier<T> applier;

		public Extractor(Class<T> clazz, Applier<T> applier) {
			this.clazz = clazz;
			this.applier = applier;
		}

		public boolean matches(ArgumentMatcher<?> matcher) {
			return this.clazz.isInstance(matcher);
		}

		@Override
		public final PactDslJsonBody apply(Invocation invocation, PactDslJsonBody body) {
			return applier.apply(invocation, body, clazz.cast(invocation.getMatcher()));
		}

	}

	private final static List<Extractor<? extends PactoMatcher<?>>> extractors = List.of( //
			x(NullValueArg.class, (i, b, m) -> b.nullValue(i.getAttribute())), //
			x(BooleanTypeArg.class, (i, b, m) -> b.booleanType(i.getAttribute(), m.getValue())), //
			x(BooleanValueArg.class, (i, b, m) -> b.booleanValue(i.getAttribute(), m.getValue())), //
			x(StringMatcherArg.class, (i, b, m) -> b.stringMatcher(i.getAttribute(), m.getRegex(), m.getValue())), //
			x(StringTypeArg.class, (i, b, m) -> b.stringType(i.getAttribute(), m.getValue())), //
			x(IntegerTypeArg.class, (i, b, m) -> b.integerType(i.getAttribute(), m.getValue())), //
			x(DecimalTypeArg.class, (i, b, m) -> b.decimalType(i.getAttribute(), m.getValue())), //
			x(NumberTypeArg.class, (i, b, m) -> b.numberType(i.getAttribute(), m.getValue())), //
			x(EachLikeArg.class, (i, b, m) -> {
				Integer max = m.getMax();
				Integer min = m.getMin();
				DslPart each = pactFrom(m.getValue());
				if (max != null) {
					return b.maxArrayLike(i.getAttribute(), max, each);
				}
				if (min != null) {
					return b.minArrayLike(i.getAttribute(), min, each);
				}
				return b.eachLike(i.getAttribute(), each);
			}));

	private PactoDslBuilder() {
		super();
	}

	public static DslPart pactFrom(Object dto) {
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
			} else if (int.class.isAssignableFrom(parameter) || Integer.class.isAssignableFrom(parameter)) {
				return body.integerType(attribute).numberValue(attribute, (int) invocation.getArg());
			} else if (long.class.isAssignableFrom(parameter) || Long.class.isAssignableFrom(parameter)) {
				return body.integerType(attribute).numberValue(attribute, (long) invocation.getArg());
			} else if (double.class.isAssignableFrom(parameter) || Double.class.isAssignableFrom(parameter)) {
				return body.decimalType(attribute).numberValue(attribute, (double) invocation.getArg());
			} else if (float.class.isAssignableFrom(parameter) || Float.class.isAssignableFrom(parameter)) {
				return body.decimalType(attribute).numberValue(attribute, (float) invocation.getArg());
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
