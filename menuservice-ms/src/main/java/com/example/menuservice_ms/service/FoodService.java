package com.example.menuservice_ms.service;

import com.example.menuservice_ms.dto.FoodAvailabilityRequest;
import com.example.menuservice_ms.dto.FoodRequest;
import com.example.menuservice_ms.exception.BadRequestException;
import com.example.menuservice_ms.exception.NotFoundException;
import com.example.menuservice_ms.model.Food;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FoodService {
	private final ConcurrentHashMap<Long, Food> foods = new ConcurrentHashMap<>();
	private final AtomicLong idSequence = new AtomicLong(0);
	private final CategoryService categoryService;

	public FoodService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public List<Food> getAll() {
		List<Food> result = new ArrayList<>(foods.values());
		result.sort(Comparator.comparingLong(Food::getId));
		return result;
	}

	public Food getById(Long id) {
		if (id == null || !foods.containsKey(id)) {
			throw new NotFoundException("Food not found: " + id);
		}
		return foods.get(id);
	}

	public Food create(FoodRequest request) {
		validateCreateRequest(request);

		long id = idSequence.incrementAndGet();
		boolean available = Boolean.TRUE.equals(request.getAvailable());
		Food food = new Food(id, request.getName().trim(), request.getDescription(), request.getCategoryId(), available);
		foods.put(id, food);
		return food;
	}

	public Food update(Long id, FoodRequest request) {
		if (id == null || !foods.containsKey(id)) {
			throw new NotFoundException("Food not found: " + id);
		}

		validateUpdateRequest(request);

		Food existing = foods.get(id);
		existing.setName(request.getName().trim());
		existing.setDescription(request.getDescription());
		existing.setCategoryId(request.getCategoryId());

		if (request.getAvailable() != null) {
			existing.setAvailable(Boolean.TRUE.equals(request.getAvailable()));
		}
		return existing;
	}

	public Food patchAvailability(Long id, FoodAvailabilityRequest request) {
		if (id == null || !foods.containsKey(id)) {
			throw new NotFoundException("Food not found: " + id);
		}
		if (request == null || request.getAvailable() == null) {
			throw new BadRequestException("`available` is required", List.of("available"));
		}

		Food existing = foods.get(id);
		existing.setAvailable(Boolean.TRUE.equals(request.getAvailable()));
		return existing;
	}

	public void delete(Long id) {
		if (id == null || !foods.containsKey(id)) {
			throw new NotFoundException("Food not found: " + id);
		}
		foods.remove(id);
	}

	private void validateCreateRequest(FoodRequest request) {
		validateUpdateRequest(request);
		if (request.getAvailable() == null) {
			throw new BadRequestException("`available` is required", List.of("available"));
		}
	}

	private void validateUpdateRequest(FoodRequest request) {
		if (request == null) {
			throw new BadRequestException("Request body is required", List.of("body"));
		}
		if (request.getName() == null || request.getName().trim().isEmpty()) {
			throw new BadRequestException("`name` is required", List.of("name"));
		}
		if (request.getCategoryId() == null) {
			throw new BadRequestException("`categoryId` is required", List.of("categoryId"));
		}
		if (!categoryService.existsById(request.getCategoryId())) {
			throw new BadRequestException("`categoryId` does not exist", List.of("categoryId"));
		}
		// description can be null/empty; not required by the table
	}
}

