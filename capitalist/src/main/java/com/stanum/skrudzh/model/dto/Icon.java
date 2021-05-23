package com.stanum.skrudzh.model.dto;

import com.stanum.skrudzh.jpa.model.IconEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * Icon
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class Icon {

    public Icon(IconEntity iconEntity) {
        id = iconEntity.getId();
        url = iconEntity.getUrl();
        category = iconEntity.getCategory().name();
    }

    @ApiModelProperty(value = "")
    private Long id = null;

    @ApiModelProperty(value = "")
    private String url = null;

    @ApiModelProperty(value = "")
    private String category = null;

    public Icon id(Long id) {
        this.id = id;
        return this;
    }

    public Icon url(String url) {
        this.url = url;
        return this;
    }

    public Icon category(String category) {
        this.category = category;
        return this;
    }

}

