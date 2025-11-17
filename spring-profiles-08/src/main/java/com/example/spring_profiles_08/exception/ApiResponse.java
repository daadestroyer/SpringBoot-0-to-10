package com.example.spring_profiles_08.exception;

import org.springframework.http.HttpStatus;


public class ApiResponse {
    public String message;
    public HttpStatus status;
    public String httpStatusCode;

    public ApiResponse(String message, HttpStatus status, String httpStatusCode) {
        this.message = message;
        this.status = status;
        this.httpStatusCode = httpStatusCode;
    }
}


