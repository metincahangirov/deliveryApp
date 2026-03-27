package com.example.menuservice_ms.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class BadRequestException extends ApiException {
	public BadRequestException(String message) {
		super(HttpStatus.BAD_REQUEST, message, List.of(message));
	}

	public BadRequestException(String message, List<String> errors) {
		super(HttpStatus.BAD_REQUEST, message, errors);
	}
}

