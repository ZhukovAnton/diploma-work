package com.stanum.skrudzh.http;

import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.CreditsApiController;
import com.stanum.skrudzh.controller.form.CreditCreationForm;
import com.stanum.skrudzh.controller.helpers.Interceptor;
import com.stanum.skrudzh.controller.response.CreditResponse;
import com.stanum.skrudzh.model.dto.Reminder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreditApiControllerHttpTest  {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private Interceptor interceptor;

    @Autowired
    private CreditsApiController creditsApiController;

    @Test
    @Disabled
    public void greetingShouldReturnDefaultMessage() throws Exception {
        when(interceptor.preHandle(any(), any(), anyBoolean())).thenReturn(true);

        String request = "http://localhost:" + port + "/users/1/credits";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "");

        CreditCreationForm form = TestUtils.createCreditForm("my_credit");

        Reminder reminder1 = new Reminder();
        reminder1.setStartDate(OffsetDateTime.parse("2021-01-13T07:52:00.000Z"));
        form.getCredit().setReminderAttributes(reminder1);

        HttpEntity<CreditCreationForm> httpEntity = new HttpEntity<>(form, headers);
        ResponseEntity<CreditResponse> stringResponseEntity = this.restTemplate.postForEntity(request, httpEntity, CreditResponse.class);
        Thread.sleep(10000);

    }
}