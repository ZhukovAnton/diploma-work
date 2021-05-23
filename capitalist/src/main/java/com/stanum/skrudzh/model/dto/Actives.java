package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * Actives
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class Actives {

    @ApiModelProperty(value = "")
    private List<Active> actives = new ArrayList<Active>();

    public Actives actives(List<Active> actives) {
        this.actives = actives;
        return this;
    }

    public Actives addActivesItem(Active activesItem) {
        this.actives.add(activesItem);
        return this;
    }

}

