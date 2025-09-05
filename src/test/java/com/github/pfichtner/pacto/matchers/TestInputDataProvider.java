package com.github.pfichtner.pacto.matchers;

import static com.github.pfichtner.pacto.matchers.PactoMatchers.hex;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.id;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.includeStr;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.nullValue;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.numberType;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.stringType;
import static com.github.pfichtner.pacto.matchers.PactoMatchers.uuid;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

public class TestInputDataProvider implements ArgumentsProvider {

	public static record TestInputData<T>(T in, Function<T, PactoMatcher<T>> creator,
			BiConsumer<TestTarget, T> consumer, Function<T, T> supplier, String toStringFormat,
			TriFunction<PactDslJsonBody, String, T, PactDslJsonBody> operator) {

		public PactoMatcher<T> pactoMatcher() {
			return creator.apply(in);
		}

		@SuppressWarnings("unchecked")
		public Class<? extends PactoMatcher<T>> type() {
			return (Class<? extends PactoMatcher<T>>) pactoMatcher().getClass();
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
						(o, a, v) -> o.uuid(a, v)) //
		).map(Arguments::of);
	}

}
