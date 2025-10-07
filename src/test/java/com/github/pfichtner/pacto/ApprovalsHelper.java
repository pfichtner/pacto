package com.github.pfichtner.pacto;

import static com.google.gson.JsonParser.parseString;

import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import au.com.dius.pact.consumer.dsl.DslPart;

public final class ApprovalsHelper {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private ApprovalsHelper() {
		super();
	}

	public static JsonObject toJson(DslPart dslFrom) {
		var root = new JsonObject();
		root.add("body", parseString(dslFrom.toString()).getAsJsonObject());
		root.add("matchingRules", gson.toJsonTree(new TreeMap<>(dslFrom.getMatchers().getMatchingRules())));
		root.add("generators", new JsonPrimitive(dslFrom.getGenerators().toString()));
		return root;
	}

	public static JsonObject scrubBodyStringArg(JsonObject json) {
		if (json.isJsonObject()) {
			JsonObject rootObj = json.getAsJsonObject();
			if (rootObj.has("body") && rootObj.get("body").isJsonObject()) {
				JsonObject body = rootObj.getAsJsonObject("body");
				if (body.has("stringArg")) {
					body.addProperty("stringArg", "$$stringArg$$");
				}
			}
		}
		return json;
	}

}
