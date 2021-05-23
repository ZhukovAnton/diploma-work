package com.stanum.skrudzh.controller.form;

import com.stanum.skrudzh.model.dto.Reminder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * IncomeSourceCreationFormIncomeSource
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class IncomeSourceCreationForm {

    private IncomeSourceCF incomeSource;

    public IncomeSourceCreationForm name(String name) {
        this.incomeSource.name = name;
        return this;
    }

    public IncomeSourceCreationForm iconUrl(String iconUrl) {
        this.incomeSource.iconUrl = iconUrl;
        return this;
    }

    public IncomeSourceCreationForm currency(String currency) {
        this.incomeSource.currency = currency;
        return this;
    }

    public IncomeSourceCreationForm monthlyPlannedCents(Long monthlyPlannedCents) {
        this.incomeSource.monthlyPlannedCents = monthlyPlannedCents;
        return this;
    }

    public IncomeSourceCreationForm rowOrder(Integer rowOrder) {
        this.incomeSource.rowOrder = rowOrder;
        return this;
    }

    public IncomeSourceCreationForm rowOrderPosition(Integer rowOrderPosition) {
        this.incomeSource.rowOrderPosition = rowOrderPosition;
        return this;
    }

    public IncomeSourceCreationForm reminderAttributes(Reminder reminderAttributes) {
        this.incomeSource.reminderAttributes = reminderAttributes;
        return this;
    }

    public IncomeSourceCreationForm prototypeKey(String prototypeKey){
        this.incomeSource.prototypeKey = prototypeKey;
        return this;
    }

    @Data
    public class IncomeSourceCF {
        @ApiModelProperty(value = "")
        private String name = null;

        @ApiModelProperty(value = "")
        private String iconUrl = null;

        @ApiModelProperty(value = "")
        private String currency = null;

        @ApiModelProperty(value = "")
        private String description = null;

        @ApiModelProperty(value = "")
        private Long monthlyPlannedCents = null;

        @ApiModelProperty(value = "")
        private Integer rowOrder = null;

        @ApiModelProperty(value = "")
        private Integer rowOrderPosition = null;

        @ApiModelProperty(value = "")
        private Reminder reminderAttributes = null;

        @ApiModelProperty(value = "")
        private String prototypeKey = null;
    }

}

