package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;


/**
 * Session
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {

    @ApiModelProperty(value = "")
    private String token = null;

    @ApiModelProperty(value = "")
    private User user = null;

    public Session token(String token) {
        this.token = token;
        return this;
    }

    public Session user(User user) {
        this.user = user;
        return this;
    }

}

