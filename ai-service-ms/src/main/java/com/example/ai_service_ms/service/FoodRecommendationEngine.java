package com.example.ai_service_ms.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.ai_service_ms.api.dto.RecommendationResponse;
import com.example.ai_service_ms.api.dto.RecommendedMenuItemResponse;
import com.example.ai_service_ms.domain.DietaryTag;
import com.example.ai_service_ms.domain.FoodCategory;
import com.example.ai_service_ms.domain.HistoricalOrder;
import com.example.ai_service_ms.domain.MenuItem;
import com.example.ai_service_ms.domain.OrderLine;
import com.example.ai_service_ms.domain.Restaurant;
import com.example.ai_service_ms.domain.UserDinerProfile;

/**
 * Lightweight scoring suitable for real-time serving: repeat item affinity, zone popularity prior,
 * and category similarity with small dietary nudges (production systems often move this to a feature store + model).
 */
@Component
public class FoodRecommendationEngine {

	private static final double WEIGHT_FREQUENCY = 0.45;
	private static final double WEIGHT_POPULARITY = 0.35;
	private static final double WEIGHT_CATEGORY = 0.20;

	public RecommendationResponse recommend(
			UUID userId,
			UserDinerProfile profile,
			List<HistoricalOrder> orders,
			List<Restaurant> restaurants
	) {
		Map<UUID, Double> itemFrequency = new HashMap<>();
		Map<FoodCategory, Double> categoryMass = new EnumMap<>(FoodCategory.class);

		for (HistoricalOrder order : orders) {
			for (OrderLine line : order.lines()) {
				itemFrequency.merge(line.menuItemId(), (double) line.quantity(), Double::sum);
				if (line.category() != FoodCategory.UNKNOWN) {
					categoryMass.merge(line.category(), (double) line.quantity(), Double::sum);
				}
			}
		}

		double maxFreq = itemFrequency.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
		if (maxFreq <= 0) {
			maxFreq = 1.0;
		}

		List<MenuItem> candidates = restaurants.stream()
				.filter(r -> r.deliveryZoneId().equals(profile.primaryDeliveryZoneId()))
				.flatMap(r -> r.menu().stream())
				.toList();

		int maxPopularity = candidates.stream().mapToInt(MenuItem::platformOrdersLast90Days).max().orElse(1);
		if (maxPopularity <= 0) {
			maxPopularity = 1;
		}

		double categoryDenominator = categoryMass.values().stream().mapToDouble(Double::doubleValue).sum();
		Map<FoodCategory, Double> categoryPrior = new EnumMap<>(FoodCategory.class);
		if (categoryDenominator > 0) {
			categoryMass.forEach((category, mass) -> categoryPrior.put(category, mass / categoryDenominator));
		}

		record ScoredItem(MenuItem item, double score, List<String> reasons) {
		}

		List<ScoredItem> ranked = new ArrayList<>();
		for (MenuItem item : candidates) {
			double frequencyNorm = itemFrequency.getOrDefault(item.menuItemId(), 0.0) / maxFreq;
			double popularityNorm = Math.log1p(item.platformOrdersLast90Days()) / Math.log1p(maxPopularity);

			double categoryAffinity = categoryPrior.getOrDefault(item.category(), 0.0);
			categoryAffinity = blendRelatedCategories(item.category(), categoryPrior, categoryAffinity);

			double dietaryNudge = dietaryNudge(profile, item);

			double rawScore = WEIGHT_FREQUENCY * frequencyNorm
					+ WEIGHT_POPULARITY * popularityNorm
					+ WEIGHT_CATEGORY * Math.min(1.0, categoryAffinity)
					+ dietaryNudge;
			double score = Math.min(1.0, rawScore);

			List<String> reasons = new ArrayList<>();
			if (frequencyNorm >= 0.34) {
				reasons.add("FREQUENTLY_ORDERED_ITEM");
			}
			if (popularityNorm >= 0.65) {
				reasons.add("POPULAR_ACROSS_DINERS_IN_ZONE");
			}
			if (categoryAffinity >= 0.20) {
				reasons.add("CATEGORY_SIMILARITY");
			}
			if (dietaryNudge > 0) {
				reasons.add("DIETARY_PREFERENCE_MATCH");
			}
			if (reasons.isEmpty()) {
				reasons.add("ZONE_MENU_EXPLORATION");
			}

			ranked.add(new ScoredItem(item, score, reasons));
		}

		ranked.sort(Comparator.comparingDouble(ScoredItem::score).reversed());

		List<RecommendedMenuItemResponse> top = ranked.stream()
				.limit(12)
				.map(s -> new RecommendedMenuItemResponse(
						s.item().menuItemId(),
						s.item().restaurantId(),
						s.item().name(),
						s.item().category(),
						roundTwoDecimals(s.score()),
						s.reasons()
				))
				.toList();

		return new RecommendationResponse(userId, top);
	}

	private static double blendRelatedCategories(
			FoodCategory itemCategory,
			Map<FoodCategory, Double> prior,
			double baseAffinity
	) {
		double blended = baseAffinity;
		if (itemCategory == FoodCategory.POKE) {
			blended += prior.getOrDefault(FoodCategory.SUSHI, 0.0) * 0.35;
		}
		if (itemCategory == FoodCategory.SUSHI) {
			blended += prior.getOrDefault(FoodCategory.POKE, 0.0) * 0.25;
		}
		return Math.min(1.0, blended);
	}

	private static double dietaryNudge(UserDinerProfile profile, MenuItem item) {
		if (!profile.dietaryTags().contains(DietaryTag.VEGETARIAN)) {
			return 0.0;
		}
		String name = item.name().toLowerCase(Locale.ROOT);
		if (name.contains("vegan")) {
			return 0.06;
		}
		if (item.category() == FoodCategory.SALAD) {
			return 0.03;
		}
		return 0.0;
	}

	private static double roundTwoDecimals(double value) {
		return Math.round(value * 100.0) / 100.0;
	}
}
