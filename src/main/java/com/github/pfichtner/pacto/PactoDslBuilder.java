package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.Pacto.invocations;
import static com.github.pfichtner.pacto.Pacto.settings;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

	private interface AttributeHandler {
		PactDslJsonBody append(PactDslJsonBody body, String attribute, Object arg, Class<?> type);
	}

	/**
	 * Matches on types and values, delegates to {@link TypeMatchingHandler} for
	 * types.
	 */
	private static class ValueMatchingHandler implements AttributeHandler {

		private final TypeMatchingHandler lenient = new TypeMatchingHandler();

		@Override
		public PactDslJsonBody append(PactDslJsonBody body, String attribute, Object arg, Class<?> type) {
			if (CharSequence.class.isAssignableFrom(type)) {
				return body.stringValue(attribute, arg.toString());
			} else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
				return lenient(body, attribute, arg, type).numberValue(attribute, (int) arg);
			} else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
				return lenient(body, attribute, arg, type).numberValue(attribute, (long) arg);
			} else if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
				return lenient(body, attribute, arg, type).numberValue(attribute, (double) arg);
			} else if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type)) {
				return lenient(body, attribute, arg, type).numberValue(attribute, (float) arg);
			} else if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
				return lenient(body, attribute, arg, type).booleanValue(attribute, (boolean) arg);
			} else if (Number.class.isAssignableFrom(type)) {
				return lenient(body, attribute, arg, type).numberValue(attribute, (Number) arg);
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

		private PactDslJsonBody lenient(PactDslJsonBody body, String attribute, Object arg, Class<?> type) {
			return lenient.append(body, attribute, arg, type);
		}

	}

	/**
	 * Only matches on types, not the values.
	 */
	private static class TypeMatchingHandler implements AttributeHandler {

		private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
		private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

		@Override
		public PactDslJsonBody append(PactDslJsonBody body, String attribute, Object arg, Class<?> type) {
			if (CharSequence.class.isAssignableFrom(type)) {
				return body.stringType(attribute, arg.toString());
			} else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
				return body.integerType(attribute, (int) arg);
			} else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
				return body.integerType(attribute, (long) arg);
			} else if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
				return body.decimalType(attribute, (double) arg);
			} else if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type)) {
				return body.decimalType(attribute, (double) (float) arg);
			} else if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
				return body.booleanType(attribute, (boolean) arg);
			} else if (Number.class.isAssignableFrom(type)) {
				return body.numberType(attribute, (Number) arg);
			} else if (Date.class.isAssignableFrom(type)) {
				return body.date(attribute, DEFAULT_DATE_FORMAT, (Date) arg);
			} else if (LocalDate.class.isAssignableFrom(type)) {
				return body.localDate(attribute, DEFAULT_DATE_FORMAT, (LocalDate) arg);
			} else if (LocalDateTime.class.isAssignableFrom(type)) {
				return body.time(attribute, DEFAULT_DATETIME_FORMAT,
						Date.from(((LocalDateTime) arg).atZone(ZoneId.systemDefault()).toInstant()));
			} else if (UUID.class.isAssignableFrom(type)) {
				return body.uuid(attribute, (UUID) arg);
			}
			return null;
		}

	}

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
			x(StringMatcherArg.class, (i, b, m) -> stringMatcher(i, b, m)), //
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
			x(EachLikeArg.class, (i, b, m) -> each(i, b, m)));

	private static PactDslJsonBody stringMatcher(Invocation invocation, PactDslJsonBody body,
			StringMatcherArg stringMatcher) {
		String value = stringMatcher.value();
		return value == null //
				? body.stringMatcher(invocation.attribute(), stringMatcher.regex()) //
				: body.stringMatcher(invocation.attribute(), stringMatcher.regex(), value);
	}

	private static PactDslJsonBody each(Invocation invocation, PactDslJsonBody body, EachLikeArg eachLike) {
		DslPart dslPart = dslFrom(eachLike.value());
		if (eachLike.min() != null && eachLike.max() != null) {
			return body.minMaxArrayLike(invocation.attribute(), eachLike.min(), eachLike.max(), dslPart);
		} else if (eachLike.max() != null) {
			return body.maxArrayLike(invocation.attribute(), eachLike.max(), dslPart);
		} else if (eachLike.min() != null) {
			return body.minArrayLike(invocation.attribute(), eachLike.min(), dslPart);
		} else {
			return body.eachLike(invocation.attribute(), dslPart);
		}
	}

	private PactoDslBuilder() {
		super();
	}

	public static DslPart dslFrom(Object object) {
		return appendInvocations(new PactDslJsonBody(), object);
	}

	private static DslPart appendInvocations(PactDslJsonBody body, Object object) {
		return appendInvocations(body, invocations(object).invocations(), settings(object));
	}

	protected static DslPart appendInvocations(PactDslJsonBody body, List<Invocation> invocations,
			PactoSettingsImpl settings) {
		List<Invocation> handleLater = new ArrayList<>();
		DslPart bodyWithInvocations = invocations.stream() //
				.reduce(body, (b, i) -> append(b, i, handleLater, settings), (b1, __) -> b1);
		return handleLater.stream() //
				.reduce(bodyWithInvocations, PactoDslBuilder::appendInvocation, (b1, __) -> b1);
	}

	private static DslPart appendInvocation(DslPart body, Invocation invocation) {
		return appendInvocations(body.object(invocation.attribute()), invocation.arg()).closeObject();
	}

	private static PactDslJsonBody append(PactDslJsonBody body, Invocation invocation, List<Invocation> handleLater,
			PactoSettingsImpl settings) {
		if (invocation.matcher() == null) {
			PactDslJsonBody bodyWithValueAppended = appendValue(body, invocation, settings);
			if (bodyWithValueAppended == null) {
				handleLater.add(invocation);
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

	private static PactDslJsonBody appendValue(PactDslJsonBody body, Invocation invocation,
			PactoSettingsImpl settings) {
		AttributeHandler handler = handler(settings);
		PactDslJsonBody typeFor = handler.append(body, invocation.attribute(), invocation.arg(), invocation.type());
		return typeFor == null //
				? handler.append(body, invocation.attribute(), invocation.arg(), invocation.arg().getClass()) //
				: typeFor;
	}

	private static AttributeHandler handler(PactoSettingsImpl settings) {
		return settings.isStrict() //
				? new ValueMatchingHandler() //
				: new TypeMatchingHandler() //
		;
	}

}
