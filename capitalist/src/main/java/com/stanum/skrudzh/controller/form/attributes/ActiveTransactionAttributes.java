package com.stanum.skrudzh.controller.form.attributes;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class ActiveTransactionAttributes {

    @ApiModelProperty
    private String sourceType;

    @ApiModelProperty
    private Long sourceId;

    @ApiModelProperty
    private Long id;
}
