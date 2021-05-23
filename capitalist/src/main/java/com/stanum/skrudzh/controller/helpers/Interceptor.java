package com.stanum.skrudzh.controller.helpers;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.SessionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.SessionRepository;
import com.stanum.skrudzh.jpa.repository.UserRepository;
import com.stanum.skrudzh.utils.CustomHeaders;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class Interceptor extends HandlerInterceptorAdapter {

    private static final String ALLOW_HEADERS = "origin, authorization, accept, content-type, x-requested-with, X-Cache-Type, " +
            "X-Cache-Use, X-Cache-Time, pragma, cache-control, x-region, x-grafana-nocache" + CustomHeaders.IOS_BUILD;

    private final SessionRepository sessionRepository;

    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("[{}] {}", request.getMethod(), request.getRequestURI());
        if (request.getServletPath().equals("/error")) {
            return true;
        }

        response.addHeader("Access-Control-Expose-Headers", ALLOW_HEADERS);
        response.addHeader("Access-Control-Allow-Headers", ALLOW_HEADERS);
        response.addHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS");
        response.addHeader("Access-Control-Max-Age", "3600");
        response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.addHeader("Access-Control-Allow-Credentials", "true");

        if (request.getMethod().equals("OPTIONS")) {
            return false;
        }

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            ApiOperation annotation = handlerMethod.getMethodAnnotation(ApiOperation.class);
            if (annotation != null && annotation.authorizations().length > 0) {
                for (Authorization authorization : annotation.authorizations()) {
                    if (authorization.value().equals(Constants.JWT_AUTH)) {
                        String token = request.getHeader(Constants.JWT_KEY);
                        if (token == null || token.isEmpty() || !token.startsWith("Token token=")) {
                            throw new AppException(HttpAppError.UNAUTHORIZED);
                        }
                        token = token.replace("Token token=", "");
                        Optional<SessionEntity> session = sessionRepository.findByToken(token);
                        if (session.isEmpty()) {
                            throw new AppException(HttpAppError.UNAUTHORIZED);
                        }
                        RequestUtil.setToken(session.get().getToken());
                        RequestUtil.setUser(session.get().getUser());
                    }
                }
            }
        }
        processTimezone(request);
        processIosBuild(request);

        UserEntity userEntity = RequestUtil.getUser();
        boolean isUserChanged;
        isUserChanged = processLocale(request, userEntity);
        isUserChanged |= processRegion(request, userEntity);

        if (isUserChanged) {
            userRepository.save(userEntity);
        }
        if (RequestUtil.getError() != null) {
            response.setStatus(RequestUtil.getError().getStatus());
        }

        return true;
    }

    private void processIosBuild(HttpServletRequest request) {
        String iosBuild = request.getHeader("X-iOS-Build");
        if(iosBuild != null) {
            RequestUtil.setIosBuild(iosBuild);
        }
    }

    private void processTimezone(HttpServletRequest request) {
        String timezone = request.getHeader("x-timezone");
        RequestUtil.setTimezone(ZoneId.of(Objects.requireNonNullElse(timezone, "Z")));
    }

    private boolean processLocale(HttpServletRequest request, UserEntity userEntity) {
        String language = request.getHeader("Accept-Language");
        if (language != null) {
            String[] tryToSplit = language.split("[-,;_]");
            if (tryToSplit.length > 0) {
                language = tryToSplit[0];
            }
        }
        if (language == null && RequestUtil.getUser() != null) {
            language = RequestUtil.getUser().getLocale();
        }
        language = Objects.requireNonNullElse(language, "en");
        RequestUtil.setLocale(new Locale(language));
        if (userEntity == null) return false;
        if (userEntity.getLocale() == null || !language.equals(userEntity.getLocale())) {
            userEntity.setLocale(language);
            return true;
        } else {
            return false;
        }
    }

    private boolean processRegion(HttpServletRequest request, UserEntity userEntity) {
        String region = request.getHeader("x-region");
        if (region != null) {
            RequestUtil.setRegion(region);
            if (userEntity == null) return false;
            if (userEntity.getRegion() == null || !region.equals(userEntity.getRegion())) {
                userEntity.setRegion(region);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
