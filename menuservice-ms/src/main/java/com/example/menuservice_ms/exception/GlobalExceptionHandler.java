package com.example.menuservice_ms.exception;

import com.example.menuservice_ms.dto.ResultDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ResultDto<Object>> handleApiException(ApiException ex) {
		return ResponseEntity
			.status(ex.getStatus())
			.body(ResultDto.fail(ex.getMessage(), ex.getErrors()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResultDto<Object>> handleUnexpectedException(Exception ex) {
		// NOTE: real project would log the exception and use a correlation id.
		String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Unexpected error";
		return ResponseEntity
			.status(500)
			.body(ResultDto.fail("Internal server error", List.of(errorMessage)));
	}
}

