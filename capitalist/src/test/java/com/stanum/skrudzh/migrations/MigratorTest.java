package com.stanum.skrudzh.migrations;

import com.stanum.skrudzh.converters.Encryptor;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Disabled
public class MigratorTest {

    @Autowired
    private Migrator migrator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Encryptor encryptor;

    @Test
    public void test() {
        List<UserEntity> all = userRepository.findAll();
    }
}
