package com.example.menuservice_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodRequest {
    private String name;
    private String description;
    private Long categoryId;
    private Boolean available;
}

