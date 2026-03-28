package com.example.menuservice_ms.service;

import com.example.menuservice_ms.dto.CategoryRequest;
import com.example.menuservice_ms.exception.BadRequestException;
import com.example.menuservice_ms.exception.NotFoundException;
import com.example.menuservice_ms.model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CategoryService {
	private final ConcurrentHashMap<Long, Category> categories = new ConcurrentHashMap<>();
	private final AtomicLong idSequence = new AtomicLong(0);

	public List<Category> getAll() {
		List<Category> result = new ArrayList<>(categories.values());
		result.sort(Comparator.comparingLong(Category::getId));
		return result;
	}

	public boolean existsById(Long id) {
		return id != null && categories.containsKey(id);
	}

	public Category create(CategoryRequest request) {
		validateRequest(request);

		long id = idSequence.incrementAndGet();
		Category category = new Category(id, request.getName().trim());
		categories.put(id, category);
		return category;
	}

	public Category update(Long id, CategoryRequest request) {
		if (!existsById(id)) {
			throw new NotFoundException("Category not found: " + id);
		}

		validateRequest(request);
		Category existing = categories.get(id);
		existing.setName(request.getName().trim());
		return existing;
	}

	public void delete(Long id) {
		if (!existsById(id)) {
			throw new NotFoundException("Category not found: " + id);
		}
		categories.remove(id);
	}

	private void validateRequest(CategoryRequest request) {
		if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
			throw new BadRequestException("Category name is required", List.of("name"));
		}
	}
}

