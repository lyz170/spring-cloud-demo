package com.mycloud.demo.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

public class AppResponse<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private String status;

	private String message;

	private LocalDateTime time;

	private T content;

	private AppResponse() {
	}

	public static <T> AppResponse<T> buildSuccessResponse(T content) {
		AppResponse<T> response = new AppResponse<>();
		response.setStatus(String.valueOf(HttpStatus.OK.value()));
		response.setMessage(HttpStatus.OK.getReasonPhrase());
		response.setTime(LocalDateTime.now());
		response.setContent(content);
		return response;
	}

	public static <T> AppResponse<T> buildFailedResponse() {
		AppResponse<T> response = new AppResponse<>();
		response.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		response.setTime(LocalDateTime.now());
		return response;
	}

	public static <T> AppResponse<T> buildFailedResponse(String message) {
		AppResponse<T> response = new AppResponse<>();
		response.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		response.setMessage(message);
		response.setTime(LocalDateTime.now());
		return response;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}
}