package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * ActiveTypes
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class ActiveTypes {

    @ApiModelProperty(value = "")
    private List<ActiveType> activeTypes = new ArrayList<ActiveType>();

    public ActiveTypes activeTypes(List<ActiveType> activeTypes) {
        this.activeTypes = activeTypes;
        return this;
    }

    public ActiveTypes addActiveTypesItem(ActiveType activeTypesItem) {
        this.activeTypes.add(activeTypesItem);
        return this;
    }

}

