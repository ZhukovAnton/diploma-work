package com.stanum.skrudzh.model.dto.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public abstract class Base {

    @ApiModelProperty(value = "db entity id")
    protected Long id = null;

}
