package com.stanum.skrudzh.utils;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import lombok.extern.slf4j.Slf4j;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Slf4j
public class CodeGenerator {

    public static String generateCode() {
        return UUID.randomUUID().toString();
    }

    public static String generateCode(int length) {
        return UUID.randomUUID().toString().subSequence(0, length).toString();
    }

    public static String generateToken() {
        Base64.Encoder encoder = Base64.getEncoder().withoutPadding();
        SecureRandom random;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        }
        catch (NoSuchAlgorithmException exception) {
            log.error(exception.getMessage(), exception);
            throw new AppException(HttpAppError.EXTERNAL_API_ERROR, exception.getMessage());
        }
        String token = encoder.encodeToString(random.generateSeed(16));
        token = token.replace("+", "Q");
        token = token.replace("/", "r");
        token = token.replace("=", "t");
        return token;
    }
}
