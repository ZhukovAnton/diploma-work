package com.stanum.skrudzh.exception;


import org.springframework.http.HttpStatus;


public enum HttpAppError implements AppError {

    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    EMAIL_CONFIRMATION_BAD_TOKEN(HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST),
    EMAIL_CONFIRMATION_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST),
    TOO_OFTEN(HttpStatus.BAD_REQUEST),
    DUPLICATED_EMAIL(HttpStatus.UNPROCESSABLE_ENTITY),
    DUPLICATED_CONNECTION(HttpStatus.UNPROCESSABLE_ENTITY),
    VALIDATION_FAILED(HttpStatus.UNPROCESSABLE_ENTITY),
    ACCESS_DENIED(HttpStatus.FORBIDDEN),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    EXTERNAL_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE),
    USER_DISABLED(HttpStatus.FORBIDDEN),
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.BAD_REQUEST),
    INVALID_PAYLOAD(HttpStatus.UNPROCESSABLE_ENTITY),
    PAYMENT_REQUIRED(HttpStatus.PAYMENT_REQUIRED),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED),
    NOT_IMPLEMENTED_YET(HttpStatus.NOT_IMPLEMENTED),
    CLIENT_ABORT_CONNECTION(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    HttpAppError(HttpStatus status) {
        this.status = status;
    }

    @Override
    public Integer getStatus() {
        return status.value();
    }

    @Override
    public String toString() {
        return this.name();
    }

    @Override
    public String getErrorCode() {
        return this.name();
    }

}
