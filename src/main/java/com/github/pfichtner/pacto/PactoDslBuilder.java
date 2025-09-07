package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.Pacto.invocations;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.github.pfichtner.pacto.matchers.BooleanTypeArg;
import com.github.pfichtner.pacto.matchers.BooleanValueArg;
import com.github.pfichtner.pacto.matchers.DateArg;
import com.github.pfichtner.pacto.matchers.DatetimeArg;
import com.github.pfichtner.pacto.matchers.DecimalTypeArg;
import com.github.pfichtner.pacto.matchers.EachLikeArg;
import com.github.pfichtner.pacto.matchers.EqualsToArg;
import com.github.pfichtner.pacto.matchers.HexValueArg;
import com.github.pfichtner.pacto.matchers.IdArg;
import com.github.pfichtner.pacto.matchers.IncludeStrArg;
import com.github.pfichtner.pacto.matchers.IntegerTypeArg;
import com.github.pfichtner.pacto.matchers.IpAddressArg;
import com.github.pfichtner.pacto.matchers.MatchUrlArg;
import com.github.pfichtner.pacto.matchers.NullValueArg;
import com.github.pfichtner.pacto.matchers.NumberTypeArg;
import com.github.pfichtner.pacto.matchers.PactoMatcher;
import com.github.pfichtner.pacto.matchers.StringMatcherArg;
import com.github.pfichtner.pacto.matchers.StringTypeArg;
import com.github.pfichtner.pacto.matchers.TimeArg;
import com.github.pfichtner.pacto.matchers.UuidArg;

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

		public boolean matches(PactoMatcher<?> matcher) {
			return this.clazz.isInstance(matcher);
		}

		private PactDslJsonBody append(Invocation invocation, PactDslJsonBody body) {
			return this.applier.append(invocation, body, clazz.cast(invocation.matcher()));
		}

	}

	private final static List<Extractor<? extends PactoMatcher<?>>> extractors = List.of( //
			x(NullValueArg.class, (i, b, m) -> b.nullValue(i.attribute())), //
			x(EqualsToArg.class, (i, b, m) -> b.equalTo(i.attribute(), m.value())), //
			x(BooleanTypeArg.class, (i, b, m) -> b.booleanType(i.attribute(), m.value())), //
			x(BooleanValueArg.class, (i, b, m) -> b.booleanValue(i.attribute(), m.value())), //
			x(StringMatcherArg.class, (i, b, m) -> b.stringMatcher(i.attribute(), m.regex(), m.value())), //
			x(StringTypeArg.class, (i, b, m) -> b.stringType(i.attribute(), m.value())), //
			x(IncludeStrArg.class, (i, b, m) -> b.includesStr(i.attribute(), m.value())), //
			x(HexValueArg.class, (i, b, m) -> b.hexValue(i.attribute(), m.value())), //
			x(IpAddressArg.class, (i, b, m) -> b.ipAddress(i.attribute())), //
			x(UuidArg.class, (i, b, m) -> b.uuid(i.attribute(), m.value())), //
			x(MatchUrlArg.class, (i, b, m) -> b.matchUrl(i.attribute(), m.value(), m.pathFragments())), //
			x(IntegerTypeArg.class, (i, b, m) -> b.integerType(i.attribute(), m.value())), //
			x(DecimalTypeArg.class, (i, b, m) -> b.decimalType(i.attribute(), m.value())), //
			x(NumberTypeArg.class, (i, b, m) -> b.numberType(i.attribute(), m.value())), //
			x(IdArg.class, (i, b, m) -> b.id(i.attribute(), m.value())), //
			x(TimeArg.class, (i, b, m) -> b.time(i.attribute(), m.value(), m.example())), //
			x(DateArg.class, (i, b, m) -> b.date(i.attribute(), m.value(), m.example())), //
			x(DatetimeArg.class, (i, b, m) -> b.datetime(i.attribute(), m.value(), m.example())), //
			x(EachLikeArg.class, (i, b, m) -> {
				DslPart each = dslFrom(m.value());
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

	public static DslPart dslFrom(Object object) {
		return appendInvocations(new PactDslJsonBody(), object);
	}

	private static DslPart appendInvocations(PactDslJsonBody body, Object object) {
		return appendInvocations(body, invocations(object).invocations());
	}

	protected static DslPart appendInvocations(PactDslJsonBody body, List<Invocation> invocations) {
		List<Invocation> handleLater = new ArrayList<>();
		DslPart bodyWithInvocations = invocations.stream() //
				.reduce(body, (b, i) -> append(b, i, handleLater), (b1, __) -> b1);
		return handleLater.stream() //
				.reduce(bodyWithInvocations, PactoDslBuilder::appendInvocation, (b1, __) -> b1);
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
		PactDslJsonBody typeFor = typeFor(body, invocation.attribute(), invocation.arg(), invocation.type());
		return typeFor == null //
				? typeFor(body, invocation.attribute(), invocation.arg(), invocation.arg().getClass()) //
				: typeFor;
	}

	private static PactDslJsonBody typeFor(PactDslJsonBody body, String attribute, Object arg, Class<?> type) {
		if (CharSequence.class.isAssignableFrom(type)) {
			return body.stringValue(attribute, arg.toString());
		} else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
			return body.integerType(attribute).numberValue(attribute, (int) arg);
		} else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
			return body.integerType(attribute).numberValue(attribute, (long) arg);
		} else if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
			return body.decimalType(attribute).numberValue(attribute, (double) arg);
		} else if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type)) {
			return body.decimalType(attribute).numberValue(attribute, (float) arg);
		} else if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
			return body.booleanType(attribute).booleanValue(attribute, (boolean) arg);
		} else if (Number.class.isAssignableFrom(type)) {
			return body.numberType(attribute).numberValue(attribute, (Number) arg);
		} else if (Date.class.isAssignableFrom(type) || LocalDate.class.isAssignableFrom(type)) {
			return body.date(attribute);
		} else if (LocalDateTime.class.isAssignableFrom(type)) {
			return body.time(attribute);
		} else if (URL.class.isAssignableFrom(type)) {
			return body.matchUrl(attribute, (String) arg);
		} else if (UUID.class.isAssignableFrom(type)) {
			return body.uuid(attribute);
		} else if (type.isArray()) {
			return body.equalTo(attribute, arg);
		}
		return null;
	}

}
