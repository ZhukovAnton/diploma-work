package com.stanum.skrudzh.controller.form;

import com.stanum.skrudzh.model.dto.Reminder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * IncomeSourceUpdatingFormIncomeSource
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class IncomeSourceUpdatingForm {

    private IncomeSourceUF incomeSource;

    public IncomeSourceUpdatingForm name(String name) {
        this.incomeSource.name = name;
        return this;
    }

    public IncomeSourceUpdatingForm iconUrl(String iconUrl) {
        this.incomeSource.iconUrl = iconUrl;
        return this;
    }

    public IncomeSourceUpdatingForm monthlyPlannedCents(Long monthlyPlannedCents) {
        this.incomeSource.monthlyPlannedCents = monthlyPlannedCents;
        return this;
    }

    public IncomeSourceUpdatingForm rowOrderPosition(Integer rowOrderPosition) {
        this.incomeSource.rowOrderPosition = rowOrderPosition;
        return this;
    }

    public IncomeSourceUpdatingForm reminderAttributes(Reminder reminderAttributes) {
        this.incomeSource.reminderAttributes = reminderAttributes;
        return this;
    }

    public IncomeSourceUpdatingForm prototypeKey(String prototypeKey) {
        this.incomeSource.prototypeKey = prototypeKey;
        return this;
    }

    @Data
    public class IncomeSourceUF {

        @ApiModelProperty(value = "")
        private String name = null;

        @ApiModelProperty(value = "")
        private String iconUrl = null;

        @ApiModelProperty(value = "")
        private String description = null;

        @ApiModelProperty(value = "")
        private Long monthlyPlannedCents = null;

        @ApiModelProperty(value = "")
        private Integer rowOrderPosition = null;

        @ApiModelProperty(value = "")
        private Reminder reminderAttributes = null;

        @ApiModelProperty(value = "")
        private String prototypeKey = null;

    }


}

