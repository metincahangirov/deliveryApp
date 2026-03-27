package com.example.menuservice_ms.controller;

import com.example.menuservice_ms.dto.FoodAvailabilityRequest;
import com.example.menuservice_ms.dto.FoodDto;
import com.example.menuservice_ms.dto.FoodRequest;
import com.example.menuservice_ms.dto.ResultDto;
import com.example.menuservice_ms.model.Food;
import com.example.menuservice_ms.service.FoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodsController {

    private final FoodService foodService;

    public FoodsController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public ResponseEntity<ResultDto<List<FoodDto>>> listFoods() {
        List<FoodDto> data = foodService.getAll()
                .stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(ResultDto.ok("Operation completed successfully", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultDto<FoodDto>> getFood(@PathVariable Long id) {
        Food food = foodService.getById(id);
        return ResponseEntity.ok(ResultDto.ok("Operation completed successfully", toDto(food)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultDto<FoodDto>> create(@RequestBody FoodRequest request) {
        Food created = foodService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResultDto.ok("Operation completed successfully", toDto(created)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultDto<FoodDto>> update(@PathVariable Long id, @RequestBody FoodRequest request) {
        Food updated = foodService.update(id, request);
        return ResponseEntity.ok(ResultDto.ok("Operation completed successfully", toDto(updated)));
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultDto<FoodDto>> patchAvailability(@PathVariable Long id, @RequestBody FoodAvailabilityRequest request) {
        Food updated = foodService.patchAvailability(id, request);
        return ResponseEntity.ok(ResultDto.ok("Operation completed successfully", toDto(updated)));
    }

    private FoodDto toDto(Food food) {
        return new FoodDto(
                food.getId(),
                food.getName(),
                food.getDescription(),
                food.getCategoryId(),
                food.isAvailable()
        );
    }
}

