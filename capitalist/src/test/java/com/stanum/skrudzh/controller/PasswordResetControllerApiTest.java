package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.controller.PasswordResetCodesApiController;
import com.stanum.skrudzh.controller.form.PasswordResetCodeCreationForm;
import com.stanum.skrudzh.controller.response.PasswordResetCodeResponse;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.service.user.EmailService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class PasswordResetControllerApiTest extends IntegrationTest {

    @Autowired
    private PasswordResetCodesApiController passwordResetCodesApiController;

    @MockBean
    private EmailService emailService;

    @Test
    public void shouldResetCode() {
        ArgumentCaptor<UserEntity> argument = ArgumentCaptor.forClass(UserEntity.class);

        String email = user.getEmail();
        PasswordResetCodeResponse response = passwordResetCodesApiController
                .passwordResetCodesPost(createPassResetForm(email)).getBody();
        Assert.assertNotNull(response);
        Mockito.verify(emailService).sendPasswordResetCodeEmail(argument.capture());
        Assert.assertEquals(user.getId(), argument.getValue().getId());
    }

    private PasswordResetCodeCreationForm createPassResetForm(String email) {
        PasswordResetCodeCreationForm form = new PasswordResetCodeCreationForm();
        PasswordResetCodeCreationForm.SendPasswordResetCodeForm sf = form.new SendPasswordResetCodeForm();
        sf.setEmail(email);
        form.setPasswordResetCode(sf);
        return form;
    }
}
