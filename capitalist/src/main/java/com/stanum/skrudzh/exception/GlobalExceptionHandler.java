package com.stanum.skrudzh.exception;

import com.stanum.skrudzh.metrics.MetricType;
import com.stanum.skrudzh.metrics.MetricsService;
import com.stanum.skrudzh.service.user.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private final List<String> adminMails = Collections.singletonList("vlasenko-pav@yandex.ru");
    private final List<String> excludeException = Arrays.asList(
            HttpAppError.ACCESS_DENIED.name(),
            HttpAppError.PAYMENT_REQUIRED.name(),
            HttpAppError.UNAUTHORIZED.name(),
            HttpAppError.CLIENT_ABORT_CONNECTION.name());

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private EmailService emailService;

    @Value("${threebaskets.notifications.errors}")
    private Boolean errorNotifications;

    private ResponseError processException(AppError error, Throwable e, HttpServletResponse response) {
        log.info("Received AppError {}", error);
        log.error("Processing error", e);
        if(!excludeException.contains(error.getErrorCode())) {
            try {
                metricsService.saveMetric(MetricType.FATAL_ERROR, System.currentTimeMillis());
                if (errorNotifications) {
                    emailService.sendTextEmail(ExceptionUtils.getStackTrace(e), adminMails);
                }
            } catch (Throwable th) {
                log.error("Error while process controller exception", th);
            }
        }
        response.setStatus(error.getStatus());
        return new ResponseError(error.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(UndeclaredThrowableException.class)
    ResponseError handleException(UndeclaredThrowableException e, HttpServletResponse response) {
        if (e.getUndeclaredThrowable() instanceof AppException) {
            return processException(((AppException) e.getUndeclaredThrowable()).getError(), e, response);
        } else {
            return processException(HttpAppError.UNKNOWN_ERROR, e.getUndeclaredThrowable(), response);
        }
    }

    @ExceptionHandler(ValidationException.class)
    Map<String, List<String>> handleValidationException(ValidationException e, HttpServletResponse response) {
        log.warn("Validation exception", e);
        response.setStatus(e.getError().getStatus());
        return e.getErrorMap();
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    ResponseError handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletResponse response) {
        return processException(HttpAppError.BAD_REQUEST, e, response);
    }

    @ExceptionHandler({ServletException.class})
    ResponseError handleServletException(ServletException e, HttpServletResponse response) {
        return processException(HttpAppError.UNKNOWN_ERROR, e, response);
    }

    @ExceptionHandler({Exception.class, Throwable.class, RuntimeException.class})
    ResponseError handleException(Exception e, HttpServletResponse response) {
        return processException(HttpAppError.UNKNOWN_ERROR, e, response);
    }

    @ExceptionHandler(AppException.class)
    ResponseError handleException(AppException e, HttpServletResponse response) {
        return processException(e.getError(), e, response);
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    ResponseError handleException(MethodArgumentNotValidException e, HttpServletResponse response) {
        return processException(HttpAppError.VALIDATION_ERROR, e, response);
    }

    @ExceptionHandler({BindException.class})
    ResponseError handleExceptionBind(BindException e, HttpServletResponse response) {
        return processException(HttpAppError.VALIDATION_ERROR, e, response);
    }

    @ExceptionHandler({ClientAbortException.class})
    ResponseError handleClientAbortException(BindException e, HttpServletResponse response) {
        return processException(HttpAppError.CLIENT_ABORT_CONNECTION, e, response);
    }
}
