package com.github.pfichtner.pacto;

import static com.google.gson.JsonParser.parseString;
import static com.jayway.jsonpath.Configuration.defaultConfiguration;

import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import au.com.dius.pact.consumer.dsl.DslPart;

public final class ApprovalsHelper {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private ApprovalsHelper() {
	}

	public static JsonObject toJson(DslPart dslFrom) {
		var root = new JsonObject();
		root.add("body", parseString(dslFrom.toString()).getAsJsonObject());
		root.add("matchingRules", gson.toJsonTree(new TreeMap<>(dslFrom.getMatchers().getMatchingRules())));
		root.add("generators", new JsonPrimitive(dslFrom.getGenerators().toString()));
		return root;
	}

	public static JsonObject scrubWithJsonPath(JsonObject json, String jsonPath, String placeholder) {
		DocumentContext context = JsonPath.parse(defaultConfiguration().jsonProvider().parse(json.toString()));
		try {
			context.set(jsonPath, placeholder);
			return JsonParser.parseString(context.jsonString()).getAsJsonObject();
		} catch (Exception e) {
			// No match found — safe to ignore
			return json;
		}
	}

}
