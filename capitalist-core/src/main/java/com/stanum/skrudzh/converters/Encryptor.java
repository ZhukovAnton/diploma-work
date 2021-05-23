package com.stanum.skrudzh.converters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import java.security.Key;
import java.util.Base64;

@Component
@Slf4j
public class Encryptor implements AttributeConverter<String, String> {
    private static final String AES = "AES";

    private final Key key;

    public Encryptor(@Value("${threebaskets.encrypt.secret}") String secret) {
        key = new SecretKeySpec(secret.getBytes(), AES);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            if(attribute == null) {
                return null;
            }
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            log.error("Error while converting {} to db column", attribute);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            if(dbData == null) {
                return null;
            }
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            log.error("Error while converting {} to entity attribute", dbData);
            throw new IllegalStateException(e);
        }
    }

}
