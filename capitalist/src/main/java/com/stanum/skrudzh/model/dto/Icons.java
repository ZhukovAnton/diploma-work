package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * Icons
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class Icons {

    @ApiModelProperty(value = "")
    private List<Icon> icons = new ArrayList<Icon>();

    public Icons icons(List<Icon> icons) {
        this.icons = icons;
        return this;
    }

    public Icons addIconsItem(Icon iconsItem) {
        this.icons.add(iconsItem);
        return this;
    }
}

