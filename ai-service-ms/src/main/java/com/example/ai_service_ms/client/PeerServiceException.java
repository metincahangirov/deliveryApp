package com.example.ai_service_ms.client;

public class PeerServiceException extends RuntimeException {

	public PeerServiceException(String message) {
		super(message);
	}

	public PeerServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
