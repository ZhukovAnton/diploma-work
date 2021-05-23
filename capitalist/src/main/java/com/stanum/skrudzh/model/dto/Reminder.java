package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stanum.skrudzh.jpa.model.ReminderEntity;
import com.stanum.skrudzh.utils.json.OffsetDateTimeDeserializer;
import com.stanum.skrudzh.utils.json.OffsetDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;


/**
 * Reminder
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@NoArgsConstructor
public class Reminder {

    public Reminder(ReminderEntity reminderEntity) {
        id = reminderEntity.getId();
        startDate = reminderEntity.getStartDate() != null
                ? reminderEntity.getStartDate().toLocalDateTime().atOffset(ZoneOffset.UTC)
                : null;
        recurrenceRule = reminderEntity.getRecurrenceRule();
        message = reminderEntity.getMessage();
    }

    @ApiModelProperty(value = "")
    private Long id = null;

    @ApiModelProperty(value = "")
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    private OffsetDateTime startDate = null;

    @ApiModelProperty(value = "")
    private String recurrenceRule = null;

    @ApiModelProperty(value = "")
    private String message = null;

    public Reminder id(Long id) {
        this.id = id;
        return this;
    }

    public Reminder recurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
        return this;
    }

    public Reminder message(String message) {
        this.message = message;
        return this;
    }


}

