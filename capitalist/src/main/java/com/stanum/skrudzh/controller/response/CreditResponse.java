package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.Credit;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * CreditResponse
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class CreditResponse {

    @ApiModelProperty(value = "")
    private Credit credit = null;

    public CreditResponse credit(Credit credit) {
        this.credit = credit;
        return this;
    }


}

