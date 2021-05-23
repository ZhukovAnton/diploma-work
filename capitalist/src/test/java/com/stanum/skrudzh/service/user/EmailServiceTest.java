package com.stanum.skrudzh.service.user;

import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.utils.RequestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Locale;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    private String email = "test@gmail.com";

//    @Test
    public void testConfirmationMail() {
        RequestUtil.setLocale(new Locale("RU"));
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        emailService.sendConfirmationEmail(userEntity, "testurl");
    }

//    @Test
    public void testSendPasswordResetCode() {
        RequestUtil.setLocale(new Locale("RU"));
        UserEntity userEntity = new UserEntity();
        userEntity.setEmailConfirmationCode("A84nbjdfn84");
        userEntity.setEmail(email);
        emailService.sendPasswordResetCodeEmail(userEntity);
    }

//    @Test
    public void testLog() {
        emailService.sendTextEmail("Staktrace\nTest message", Collections.singletonList(email));
    }
}
