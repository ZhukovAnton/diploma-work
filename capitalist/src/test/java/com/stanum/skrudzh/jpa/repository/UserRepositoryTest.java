package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.converters.Encryptor;
import com.stanum.skrudzh.jpa.model.UserEntity;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.Optional;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private Encryptor encryptor;

    @Test
    public void testEncrypt() {
        UserEntity user = new UserEntity();
        user.setEmail("qweqwe");
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        user.setLocale("");
        user.setDefaultCurrency("RUB");

        UserEntity savedUser = repository.save(user);

        Optional<UserEntity> byId = repository.findById(savedUser.getId());
        Optional<UserEntity> byEmail = repository.findByEmail(encryptor.convertToDatabaseColumn("qweqwe"));

        Assert.assertTrue(byId.isPresent());
        Assert.assertTrue(byEmail.isPresent());
    }
}

