package com.stanum.skrudzh.exception;

public class AppException extends RuntimeException {

    private AppError error;

    public AppException(AppError error) {
        this.error = error;
    }

    public AppException(AppError error, String message) {
        super(message);
        this.error = error;
    }

    AppError getError() {
        return error;
    }

}
