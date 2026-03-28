package com.example.menuservice_ms.controller;

import com.example.menuservice_ms.dto.CategoryDto;
import com.example.menuservice_ms.dto.CategoryRequest;
import com.example.menuservice_ms.dto.ResultDto;
import com.example.menuservice_ms.model.Category;
import com.example.menuservice_ms.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    private final CategoryService categoryService;

    public CategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ResultDto<List<CategoryDto>>> listCategories() {
        List<CategoryDto> data = categoryService.getAll()
                .stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(ResultDto.ok("Operation completed successfully", data));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultDto<CategoryDto>> create(@RequestBody CategoryRequest request) {
        Category created = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResultDto.ok("Operation completed successfully", toDto(created)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultDto<CategoryDto>> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        Category updated = categoryService.update(id, request);
        return ResponseEntity.ok(ResultDto.ok("Operation completed successfully", toDto(updated)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultDto<Map<String, Object>>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ResultDto.ok("Operation completed successfully", Map.<String, Object>of()));
    }

    private CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}

