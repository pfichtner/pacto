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

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		var root = new JsonObject();
		root.add("body", parseString(dslFrom.toString()).getAsJsonObject());
		root.add("matchingRules", gson.toJsonTree(new TreeMap<>(dslFrom.getMatchers().getMatchingRules())));
		verifyAsJson(root);
	}

	private static ArgumentProvider argumentProvider() {
		return (m, t) -> {
			if (t == String.class) {
				if ("hex".equals(m)) {
					return "00FF";
				} else if ("uuid".equals(m)) {
					return "e2490de5-5bd3-43d5-b7c4-526e33f71304";
				} else if ("ipAddress".equals(m)) {
					return "127.0.0.1";
				} else if ("date".equals(m)) {
					return "yyyy";
				} else if ("time".equals(m) || "datetime".equals(m)) {
					return "HH:MM";
				}
				return "example";
			} else if (t == int.class || t == Integer.class) {
				return 42;
			} else if (t == long.class || t == Long.class) {
				return 123L;
			} else if (t == double.class || t == Double.class) {
				return 3.14;
			} else if (t == float.class || t == Float.class) {
				return 2.71f;
			} else if (t == boolean.class || t == Boolean.class) {
				return true;
			} else if (t == UUID.class) {
				return UUID.randomUUID();
			} else if (t == Date.class) {
				return new Date();
			} else if (t == java.time.LocalDate.class) {
				return LocalDate.now();
			} else if (t == java.time.LocalDateTime.class) {
				return LocalDateTime.now();
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
