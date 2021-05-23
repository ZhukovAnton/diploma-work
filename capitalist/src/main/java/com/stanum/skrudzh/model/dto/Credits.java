package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * Credits
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class Credits {

    @ApiModelProperty(value = "")
    private List<Credit> credits = new ArrayList<Credit>();

    public Credits credits(List<Credit> credits) {
        this.credits = credits;
        return this;
    }

    public Credits addCreditsItem(Credit creditsItem) {
        this.credits.add(creditsItem);
        return this;
    }

}

