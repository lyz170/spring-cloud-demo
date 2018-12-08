package com.mycloud.demo.config;

public class AppException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AppException(String message) {
		super(message);
	}

	public AppException(Throwable cause) {
		super(cause);
	}
}
