package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.Pacto.spec;
import static com.github.pfichtner.pacto.PactoDslBuilder.dslFrom;
import static com.google.gson.JsonParser.parseString;
import static org.approvaltests.JsonApprovals.verifyAsJson;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.pfichtner.pacto.StaticMethodInvoker.ArgumentProvider;
import com.github.pfichtner.pacto.matchers.PactoMatchers;
import com.github.pfichtner.pacto.matchers.TestTarget;
import com.github.pfichtner.pacto.testdata.Foo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import au.com.dius.pact.consumer.dsl.DslPart;

class ObjectTest {

	@Test
	void verifyObject() throws Exception {
		TestTarget spec = spec(new TestTarget());
		new StaticMethodInvoker(PactoMatchers.class, spec, argumentProvider()).invoke();
		DslPart dslFrom = dslFrom(spec);

		Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
		var root = new JsonObject();
		root.add("body", parseString(dslFrom.toString()).getAsJsonObject());
		root.add("matchingRules", gson.toJsonTree(new TreeMap<>(dslFrom.getMatchers().getMatchingRules())));
		verifyAsJson(root);
	}

	private static ArgumentProvider argumentProvider() {
		return (m, t) -> {
			DeterministicDataFactory random = new DeterministicDataFactory(m + t);
			if (CharSequence.class.isAssignableFrom(t)) {
				if ("hex".equals(m)) {
					return random.hexString(4);
				} else if ("uuid".equals(m)) {
					return ((UUID) argumentProvider().getArgument(m, UUID.class)).toString();
				} else if ("ipAddress".equals(m)) {
					return random.ipv4Address();
				} else if ("date".equals(m)) {
					return "yyyy-MM";
				} else if ("time".equals(m) || "datetime".equals(m)) {
					return "hh:mm";
				}
				return random.string(32);
			} else if (t == int.class || t == Integer.class) {
				return random.intValue();
			} else if (t == long.class || t == Long.class) {
				return random.longValue();
			} else if (t == double.class || t == Double.class) {
				return random.doubleValue();
			} else if (t == float.class || t == Float.class) {
				return random.floatValue();
			} else if (t == boolean.class || t == Boolean.class) {
				return random.booleanValue();
			} else if (t == UUID.class) {
				return random.uuid();
			} else if (t == Date.class) {
				return random.date();
			} else if (t == LocalDate.class) {
				return random.localDate();
			} else if (t == LocalDateTime.class) {
				return random.localDateTime();
			} else if (t.isArray()) {
				return Array.newInstance(t.getComponentType(), 0);
			} else if (List.class.isAssignableFrom(t)) {
				return List.of();
			} else if (Set.class.isAssignableFrom(t)) {
				return Set.of();
			} else {
				return spec(new Foo());
			}
		};
	}

}
