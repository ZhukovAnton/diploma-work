package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.Active;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * ActiveResponse
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class ActiveResponse {

    @ApiModelProperty(value = "")
    private Active active = null;

    public ActiveResponse active(Active active) {
        this.active = active;
        return this;
    }
}

