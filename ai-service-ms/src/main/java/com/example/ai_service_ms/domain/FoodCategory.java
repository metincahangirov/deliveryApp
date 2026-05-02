package com.example.ai_service_ms.domain;

/**
 * Normalized menu taxonomy used for category-similarity scoring (e.g. "users who order sushi also see poke").
 */
public enum FoodCategory {
	PIZZA,
	BURGER,
	SUSHI,
	POKE,
	SALAD,
	DESSERT,
	DRINK,
	UNKNOWN;

	public static FoodCategory fromApiCode(String code) {
		if (code == null || code.isBlank()) {
			return UNKNOWN;
		}
		try {
			return FoodCategory.valueOf(code.trim().toUpperCase());
		}
		catch (IllegalArgumentException ex) {
			return UNKNOWN;
		}
	}
}
