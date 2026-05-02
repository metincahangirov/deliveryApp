package com.example.ai_service_ms.llm;

public class LlmException extends RuntimeException {

	public LlmException(String message) {
		super(message);
	}

	public LlmException(String message, Throwable cause) {
		super(message, cause);
	}
}
