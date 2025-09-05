package com.github.pfichtner.pacto.matchers;

import static com.github.pfichtner.pacto.matchers.PactoMatchers.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

public class TestInputDataProvider implements ArgumentsProvider {

	public static record TestInputData<T>(T in, Function<T, PactoMatcher<T>> matcherCreator,
			BiConsumer<TestTarget, T> consumer, Function<T, T> supplier, String toStringFormat,
			TriFunction<PactDslJsonBody, String, T, PactDslJsonBody> operator) {

		public PactoMatcher<T> matcher() {
			return matcherCreator.apply(in);
		}

		@SuppressWarnings("unchecked")
		public Class<? extends PactoMatcher<T>> type() {
			return (Class<? extends PactoMatcher<T>>) matcher().getClass();
		}

		public TestTarget handle(TestTarget target) {
			consumer.accept(target, supplier.apply(in));
			return target;
		}

		public PactDslJsonBody handle(PactDslJsonBody body, String attribute) {
			return operator.apply(body, attribute, in);
		}

	}

	@Override
	public Stream<Arguments> provideArguments(ExtensionContext context) throws Exception {
		Object nullVal = null;
		UUID uuid = UUID.fromString("5d9c57fe-d2ea-42aa-b2f1-d203d6bb6cb5");
		String hex = "0000FFFF";
		String string = "xyz";
		Number number = 123;
		long longVal = 123L;
		String timeFormat = "HH:mm";
		String dateFormat = "yyyy-MM-dd";
		String datetimeFormat = "yyyy-MM-dd HH:mm";
		Date date = new GregorianCalendar(2025, 8, 5, 18, 25).getTime();
		String ipAddress = "127.0.0.1";

		return Stream.of( //
				new TestInputData<>(nullVal, o -> new NullValueArg(), (o, v) -> o.objectArg(v), __ -> nullValue(),
						"nullValue", (o, a, v) -> o.nullValue(a)), //
				new TestInputData<>(string, o -> new StringTypeArg(o), (o, v) -> o.stringArg(v), v -> stringType(v),
						"stringType(%s)", (o, a, v) -> o.stringType(a, v)), //
				new TestInputData<>(string, o -> new IncludeStrArg(o), (o, v) -> o.stringArg(v), v -> includeStr(v),
						"includeStr(%s)", (o, a, v) -> o.includesStr(a, v)), //
				new TestInputData<>(hex, o -> new HexValueArg(o), (o, v) -> o.stringArg(v), v -> hex(v), "hex(%s)",
						(o, a, v) -> o.hexValue(a, v)), //
				new TestInputData<>(number, o -> new NumberTypeArg(o), (o, v) -> o.numberArg(v), v -> numberType(v),
						"numberType(%s)", (o, a, v) -> o.numberType(a, v)), //
				new TestInputData<>(longVal, o -> new IdArg(o), (o, v) -> o.numberArg(v), v -> id(v), "id(%s)",
						(o, a, v) -> o.id(a, v)), //
				new TestInputData<>(uuid, o -> new UuidArg(o), (o, v) -> o.uuidArg(v), v -> uuid(v), "uuid(%s)",
						(o, a, v) -> o.uuid(a, v)), //
				new TestInputData<>(timeFormat, o -> new TimeArg(o, date), (o, v) -> o.dateArg(date), v -> {
					time(v, date);
					return v;
				}, "time(%s,18:25)", (o, a, v) -> o.time(a, v, date)), //
				new TestInputData<>(dateFormat, o -> new DateArg(o, date), (o, v) -> o.dateArg(date), v -> {
					date(v, date);
					return v;
				}, "date(%s,2025-09-05)", (o, a, v) -> o.date(a, v, date)), //
				new TestInputData<>(datetimeFormat, o -> new DatetimeArg(o, date), (o, v) -> o.dateArg(date), v -> {
					datetime(v, date);
					return v;
				}, "datetime(%s,2025-09-05 18:25)", (o, a, v) -> o.datetime(a, v, date)), //
				new TestInputData<>(ipAddress, o -> new IpAddressArg(), (o, v) -> o.stringArg(v), v -> ipAddress(v),
						"ipAddress", (o, a, v) -> o.ipAddress(a)) //

		).map(Arguments::of);
	}

}
