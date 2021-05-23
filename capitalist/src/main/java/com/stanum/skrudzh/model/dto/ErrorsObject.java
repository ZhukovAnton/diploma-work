package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * ErrorsObject
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class ErrorsObject {

    @ApiModelProperty(value = "")
    private ErrorsMap errors = null;

    public ErrorsObject errors(ErrorsMap errors) {
        this.errors = errors;
        return this;
    }

}

