package com.example.menuservice_ms.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class NotFoundException extends ApiException {
	public NotFoundException(String message) {
		super(HttpStatus.NOT_FOUND, message, List.of(message));
	}
}

