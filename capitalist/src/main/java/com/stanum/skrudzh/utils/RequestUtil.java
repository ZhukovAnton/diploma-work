package com.stanum.skrudzh.utils;

import com.stanum.skrudzh.exception.AppError;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import com.stanum.skrudzh.utils.constant.Constants;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

public class RequestUtil {

    private static void setProp(String key, Object value) {
        if (RequestContextHolder.getRequestAttributes() != null) {
            RequestContextHolder.currentRequestAttributes().setAttribute(key, value, RequestAttributes.SCOPE_REQUEST);
        }
    }

    private static Object getProp(String key) {
        Object value = null;
        try {
            value = RequestContextHolder.currentRequestAttributes().getAttribute(key, RequestAttributes.SCOPE_REQUEST);
        } catch (Exception e) {
            //nothing to do
        }
        return value;
    }

    public static Timestamp transformIntoUsersTime(Timestamp timestamp) {
        if (RequestUtil.getTimeZone() == null) return timestamp; //must be unreachable
        return  Timestamp.valueOf(
                LocalDateTime.ofInstant(
                        ZonedDateTime.ofInstant(
                                timestamp.toLocalDateTime(),
                                RequestUtil.getTimeZone().getRules().getOffset(timestamp.toInstant()),
                                RequestUtil.getTimeZone()).toInstant(),
                        ZoneId.of("Z"))); //this transformation is need,
        // because user request time is in users timezone, but without indicating
        // of this timezone
    }

    public static String getToken() {
        return (String) getProp(Constants.JWT_KEY);
    }

    public static void setToken(String token) {
        setProp(Constants.JWT_KEY, token);
    }

    public static UserEntity getUser() {
        return (UserEntity) getProp(Constants.USER);
    }

    public static void setUser(UserEntity id) {
        setProp(Constants.USER, id);
    }

    public static AppError getError() {
        return (AppError) getProp("code");
    }

    public static void setError(AppError e) {
        setProp("code", e);
    }

    public static Locale getLocale() {
        return (Locale) getProp("locale");
    }

    public static void setLocale(Locale l) {
        setProp("locale", l);
    }

    public static void setIosBuild(String iosBuild) {
        setProp(CustomHeaders.IOS_BUILD, iosBuild);
    }

    public static String getIosBuild() {
        return (String)getProp(CustomHeaders.IOS_BUILD);
    }

    public static String getRegion() {
        return (String) getProp("region");
    }

    public static void setRegion(String region) {
        setProp("region", region);
    }

    public static void setTimezone(ZoneId tz) {
        setProp("timezone", tz);
    }

    public static ZoneId getTimeZone() {
        return (ZoneId) getProp("timezone");
    }

    public static boolean isNeedAuthorize() {
        return getToken() != null;
    }

    public static boolean hasGlobalSorting() {
        String iosBuild = getIosBuild();
        //TODO define version
        return iosBuild != null && !iosBuild.isEmpty() && iosBuild.equals("v2");
    }


    public static Timestamp getBeginningOfDefaultPeriod(UserEntity userEntity) {
        return getBeginningOfPeriod(userEntity.getDefaultPeriod());
    }

    public static Timestamp getEndOfDefaultPeriod(UserEntity userEntity) {
        return getEndOfPeriod(userEntity.getDefaultPeriod());
    }

    public static Timestamp getBeginningOfPeriod(PeriodEnum periodEnum) {
        if (periodEnum.equals(PeriodEnum.day)) {
            return transformIntoUsersTime(TimeUtil.beginningOfDay());
        } else if (periodEnum.equals(PeriodEnum.week)) {
            return transformIntoUsersTime(TimeUtil.beginningOfWeek());
        } else if (periodEnum.equals(PeriodEnum.month)) {
            return transformIntoUsersTime(TimeUtil.beginningOfMonth());
        } else if (periodEnum.equals(PeriodEnum.quarter)) {
            return transformIntoUsersTime(TimeUtil.beginningOfQuarter());
        } else {
            return transformIntoUsersTime(TimeUtil.beginningOfYear());
        }
    }

    public static Timestamp getEndOfPeriod(PeriodEnum periodEnum) {
        if (periodEnum.equals(PeriodEnum.day)) {
            return transformIntoUsersTime(TimeUtil.endOfDay());
        } else if (periodEnum.equals(PeriodEnum.week)) {
            return transformIntoUsersTime(TimeUtil.endOfWeek());
        } else if (periodEnum.equals(PeriodEnum.month)) {
            return transformIntoUsersTime(TimeUtil.endOfMonth());
        } else if (periodEnum.equals(PeriodEnum.quarter)) {
            return transformIntoUsersTime(TimeUtil.endOfQuarter());
        } else {
            return transformIntoUsersTime(TimeUtil.endOfYear());
        }
    }

}
