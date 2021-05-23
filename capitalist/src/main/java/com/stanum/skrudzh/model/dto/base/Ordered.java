package com.stanum.skrudzh.model.dto.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public abstract class Ordered extends Base {

    @ApiModelProperty(value = "")
    protected Integer rowOrder = null;

}
