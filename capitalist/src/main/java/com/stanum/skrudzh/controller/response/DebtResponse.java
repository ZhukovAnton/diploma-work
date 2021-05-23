package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.Borrow;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * InlineResponse2003
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class DebtResponse {

    @ApiModelProperty(value = "")
    private Borrow debt = null;

    public DebtResponse debt(Borrow debt) {
        this.debt = debt;
        return this;
    }

}

