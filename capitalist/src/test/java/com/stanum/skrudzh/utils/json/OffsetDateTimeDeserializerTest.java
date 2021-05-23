package com.stanum.skrudzh.utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stanum.skrudzh.model.dto.Reminder;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OffsetDateTimeDeserializerTest {

    @Test
    public void shouldParseShortFormat() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String reminderString = "{\n" +
                "      \"id\": 0,\n" +
                "      \"message\": \"string\",\n" +
                "      \"recurrenceRule\": \"string\",\n" +
                "      \"startDate\": \"2020-12-24T04:58:23.271Z\"\n" +
                "    }";


        Reminder reminder = objectMapper.readValue(reminderString, Reminder.class);
        Assert.assertNotNull(reminder);
    }

    @Test
    public void shouldParseFullFormat() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String reminderString = "{\n" +
                "      \"id\": 0,\n" +
                "      \"message\": \"string\",\n" +
                "      \"recurrenceRule\": \"string\",\n" +
                "      \"startDate\": \"2020-12-24T04:58:23.271111Z\"\n" +
                "    }";


        Reminder reminder = objectMapper.readValue(reminderString, Reminder.class);
        Assert.assertNotNull(reminder);
    }

    @Test
    public void shouldThrowJsonMappingException_ifUnknownFormat() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String reminderString = "{\n" +
                "      \"id\": 0,\n" +
                "      \"message\": \"string\",\n" +
                "      \"recurrenceRule\": \"string\",\n" +
                "      \"startDate\": \"2020-12asdads-24T04:58:23.271111Z\"\n" +
                "    }";
        Assertions.assertThrows(com.fasterxml.jackson.databind.JsonMappingException.class, () -> {
            objectMapper.readValue(reminderString, Reminder.class);
        });

    }
}
