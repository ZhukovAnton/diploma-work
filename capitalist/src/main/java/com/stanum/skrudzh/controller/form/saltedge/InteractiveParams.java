package com.stanum.skrudzh.controller.form.saltedge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteractiveParams {
    private String name;
    private String value;

    @Override
    public String toString() {
        return "\"" + name + "\":" +
                "\"" + value + "\"";
    }
}
