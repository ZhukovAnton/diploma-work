package com.stanum.skrudzh.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.model.dto.ActiveTypes;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;

@SpringBootTest
public class ActiveTypesApiControllerTest extends IntegrationTest {
    private static final String SHORT_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String FULL_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX";
    private static final DateTimeFormatter FULL_FORMAT = DateTimeFormatter.ofPattern(FULL_FORMAT_STRING);
    private static final DateTimeFormatter SHORT_FORMAT = DateTimeFormatter.ofPattern(SHORT_FORMAT_STRING);

    @Autowired
    private ActiveTypesApiController activeTypesApiController;

    @Autowired
    private ObjectMapper objectMapper;

//    @Test
//    public void test3() throws Exception {
//        String date = "2020-12-12T12:12:12.123Z";
//        OffsetDateTime parse = OffsetDateTime.parse(date, SHORT_FORMAT);
//
//        LocalDateTime dt = LocalDateTime.parse(date, SHORT_FORMAT);
//        ZonedDateTime t = ZonedDateTime.parse(date, SHORT_FORMAT);
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        String s = objectMapper.writeValueAsString(parse);
//        String s4 = objectMapper.writeValueAsString(dt);
//        String s45= objectMapper.writeValueAsString(t);
//
//        ZonedDateTime z = ZonedDateTime.of(Timestamp.from(Instant.now()).toLocalDateTime(), ZoneId.of("Z"));
//        String s333= objectMapper.writeValueAsString(z);
//
//
//    }


    @Test
    public void test() {
        ActiveTypes activeTypes = activeTypesApiController.activeTypesGet("").getBody();
        Assert.assertEquals(4, activeTypes.getActiveTypes().size());
    }
}
