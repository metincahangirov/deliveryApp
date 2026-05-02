package com.example.ai_service_ms.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.ai_service_ms.client.PeerServiceException;
import com.example.ai_service_ms.llm.LlmException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(PeerServiceException.class)
	ResponseEntity<ErrorResponse> handlePeer(PeerServiceException ex) {
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
				.body(new ErrorResponse("PEER_SERVICE_ERROR", ex.getMessage()));
	}

	@ExceptionHandler(IllegalStateException.class)
	ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse("INTERNAL_ERROR", ex.getMessage()));
	}

	@ExceptionHandler(LlmException.class)
	ResponseEntity<ErrorResponse> handleLlm(LlmException ex) {
		HttpStatus status = ex.getMessage() != null && ex.getMessage().startsWith("LLM HTTP")
				? HttpStatus.BAD_GATEWAY
				: HttpStatus.SERVICE_UNAVAILABLE;
		String code = status == HttpStatus.BAD_GATEWAY ? "LLM_UPSTREAM_ERROR" : "LLM_NOT_AVAILABLE";
		return ResponseEntity.status(status).body(new ErrorResponse(code, ex.getMessage()));
	}

	public record ErrorResponse(String error, String message) {
	}
}
