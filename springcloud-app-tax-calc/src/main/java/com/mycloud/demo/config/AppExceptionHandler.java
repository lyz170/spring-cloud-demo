package com.mycloud.demo.config;

import com.mycloud.demo.model.AppResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {AppException.class})
    protected ResponseEntity<Object> handle(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(AppResponse.buildFailedResponse(getRootCause(ex).getMessage()), new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static Throwable getRootCause(Throwable e) {
        Throwable cause = null;
        Throwable result = e;
        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }
}
