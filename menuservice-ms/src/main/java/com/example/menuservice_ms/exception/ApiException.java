package com.example.menuservice_ms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class ApiException extends RuntimeException {
	private final HttpStatus status;
	private final List<String> errors;

	public ApiException(HttpStatus status, String message, List<String> errors) {
		super(message);
		this.status = status;
		this.errors = errors;
	}
}

