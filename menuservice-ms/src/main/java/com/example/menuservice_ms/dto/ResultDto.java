package com.example.menuservice_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto<T> {
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;

    public static <T> ResultDto<T> ok(String message, T data) {
        return new ResultDto<>(true, message, data, List.of());
    }

    public static <T> ResultDto<T> fail(String message, List<String> errors) {
        return new ResultDto<>(false, message, null, errors);
    }
}

