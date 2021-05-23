package com.stanum.skrudzh.controller.form.attributes;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class PlannedIncomeSavingAttributes {

    @ApiModelProperty
    private Long percentCents;
}
