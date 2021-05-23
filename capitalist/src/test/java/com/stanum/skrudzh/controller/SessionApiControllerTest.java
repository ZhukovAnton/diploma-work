package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.controller.form.SessionCredentialsForm;
import com.stanum.skrudzh.controller.response.SessionResponse;
import com.stanum.skrudzh.jpa.model.SessionEntity;
import com.stanum.skrudzh.jpa.repository.SessionRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class SessionApiControllerTest extends IntegrationTest {

    @Autowired
    private SessionsApiController sessionsApiController;

    @Autowired
    private SessionRepository sessionRepository;

    @Test
    @Disabled
    public void shouldCreateSession() {
        SessionResponse session = sessionsApiController.create(credentialsForm(), null).getBody();
        Assert.assertNotNull(session.getSession().getUser());
        Assert.assertNotNull(session.getSession().getToken());
    }

    @Test
    @Disabled
    public void shouldDestroySession() {
        SessionResponse session = sessionsApiController.create(credentialsForm(), null).getBody();
        Assert.assertNotNull(session.getSession().getUser());
        Assert.assertNotNull(session.getSession().getToken());
        sessionsApiController.destroy(session.getSession().getToken(), null);

        Optional<SessionEntity> destroyedSession = sessionRepository.findByToken(session.getSession().getToken());
        Assert.assertFalse(destroyedSession.isPresent());
    }

    private SessionCredentialsForm credentialsForm() {
        SessionCredentialsForm form = new SessionCredentialsForm();
        SessionCredentialsForm.SessionForm sf =form.new SessionForm();
        sf.setEmail(user.getEmail());
        sf.setPassword("pass");
        form.setSession(sf);
        return form;
    }
}
