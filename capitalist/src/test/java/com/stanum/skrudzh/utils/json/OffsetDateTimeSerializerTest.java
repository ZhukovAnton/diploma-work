package com.stanum.skrudzh.utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stanum.skrudzh.model.dto.Reminder;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

public class OffsetDateTimeSerializerTest {


    @Test
    public void shouldParseShortFormat() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2021-01-13T07:52:00.000Z");
        Reminder reminder = new Reminder();
        reminder.setStartDate(offsetDateTime);

        String reminderString = objectMapper.writeValueAsString(reminder);
        System.out.println(reminderString);
        Assert.assertNotNull(reminderString);
    }


}
