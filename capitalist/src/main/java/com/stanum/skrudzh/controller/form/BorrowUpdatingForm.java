package com.stanum.skrudzh.controller.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;


/**
 * BorrowUpdatingForm
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class BorrowUpdatingForm {

    @ApiModelProperty(value = "")
    private String name = null;

    @ApiModelProperty(value = "")
    private String iconUrl = null;

    @ApiModelProperty(value = "")
    private Long amountCents = null;

    @ApiModelProperty(value = "")
    private String comment = null;

    @ApiModelProperty(value = "")
    private LocalDateTime borrowedAt = null;

    @ApiModelProperty(value = "")
    private LocalDateTime payday = null;

    @ApiModelProperty(value = "")
    private Integer rowOrderPosition = null;

    public BorrowUpdatingForm name(String name) {
        this.name = name;
        return this;
    }

    public BorrowUpdatingForm iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public BorrowUpdatingForm amountCents(Long amountCents) {
        this.amountCents = amountCents;
        return this;
    }

    public BorrowUpdatingForm comment(String comment) {
        this.comment = comment;
        return this;
    }

    public BorrowUpdatingForm rowOrderPosition(Integer rowOrderPosition) {
        this.rowOrderPosition = rowOrderPosition;
        return this;
    }


}

