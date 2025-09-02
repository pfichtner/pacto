package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.Pacto.invocations;

import java.util.ArrayList;
import java.util.List;

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

	private static class Extractor<T extends PactoMatcher<?>> {

		@FunctionalInterface
		static interface Applier<T> {
			PactDslJsonBody append(Invocation invocation, PactDslJsonBody body, T matcher);
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

		private PactDslJsonBody append(Invocation invocation, PactDslJsonBody body) {
			return this.applier.append(invocation, body, clazz.cast(invocation.matcher()));
		}

	}

	private final static List<Extractor<? extends PactoMatcher<?>>> extractors = List.of( //
			x(NullValueArg.class, (i, b, m) -> b.nullValue(i.attribute())), //
			x(BooleanTypeArg.class, (i, b, m) -> b.booleanType(i.attribute(), m.value())), //
			x(BooleanValueArg.class, (i, b, m) -> b.booleanValue(i.attribute(), m.value())), //
			x(StringMatcherArg.class, (i, b, m) -> b.stringMatcher(i.attribute(), m.regex(), m.value())), //
			x(StringTypeArg.class, (i, b, m) -> b.stringType(i.attribute(), m.value())), //
			x(IntegerTypeArg.class, (i, b, m) -> b.integerType(i.attribute(), m.value())), //
			x(DecimalTypeArg.class, (i, b, m) -> b.decimalType(i.attribute(), m.value())), //
			x(NumberTypeArg.class, (i, b, m) -> b.numberType(i.attribute(), m.value())), //
			x(EachLikeArg.class, (i, b, m) -> {
				DslPart each = pactFrom(m.value());
				if (m.max() != null) {
					return b.maxArrayLike(i.attribute(), m.max(), each);
				} else if (m.min() != null) {
					return b.minArrayLike(i.attribute(), m.min(), each);
				} else {
					return b.eachLike(i.attribute(), each);
				}
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
		List<Invocation> handleLater = new ArrayList<>();
		DslPart bodyWithInvocations = invocations.stream() //
				.reduce(body, (b, i) -> append(b, i, handleLater), (b1, b2) -> b1);
		return handleLater.stream() //
				.reduce(bodyWithInvocations, PactoDslBuilder::appendInvocation, (b1, b2) -> b1);
	}

	private static DslPart appendInvocation(DslPart body, Invocation invocation) {
		return appendInvocations(body.object(invocation.attribute()), invocation.arg()).closeObject();
	}

	private static PactDslJsonBody append(PactDslJsonBody body, Invocation invocation,
			List<Invocation> pushbackInvocations) {
		if (invocation.matcher() == null) {
			PactDslJsonBody bodyWithValueAppended = appendValue(body, invocation);
			if (bodyWithValueAppended == null) {
				pushbackInvocations.add(invocation);
				return body;
			}
			return bodyWithValueAppended;
		}

		return extractors.stream() //
				.filter(e -> e.matches(invocation.matcher())) //
				.findFirst() //
				.map(e -> e.append(invocation, body)) //
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("Cannot handle %s (%s)", invocation.attribute(), invocation)));
	}

	private static PactDslJsonBody appendValue(PactDslJsonBody body, Invocation invocation) {
		String attribute = invocation.attribute();
		Class<?> parameter = invocation.type();
		if (CharSequence.class.isAssignableFrom(parameter)) {
			return body.stringValue(attribute, invocation.arg().toString());
		} else if (int.class.isAssignableFrom(parameter) || Integer.class.isAssignableFrom(parameter)) {
			return body.integerType(attribute).numberValue(attribute, (int) invocation.arg());
		} else if (long.class.isAssignableFrom(parameter) || Long.class.isAssignableFrom(parameter)) {
			return body.integerType(attribute).numberValue(attribute, (long) invocation.arg());
		} else if (double.class.isAssignableFrom(parameter) || Double.class.isAssignableFrom(parameter)) {
			return body.decimalType(attribute).numberValue(attribute, (double) invocation.arg());
		} else if (float.class.isAssignableFrom(parameter) || Float.class.isAssignableFrom(parameter)) {
			return body.decimalType(attribute).numberValue(attribute, (float) invocation.arg());
		} else if (boolean.class.isAssignableFrom(parameter) || Boolean.class.isAssignableFrom(parameter)) {
			return body.booleanType(attribute).booleanValue(attribute, (boolean) invocation.arg());
		} else if (Number.class.isAssignableFrom(parameter)) {
			return body.numberType(attribute).numberValue(attribute, (Number) invocation.arg());
		}
		return null;
	}

}
