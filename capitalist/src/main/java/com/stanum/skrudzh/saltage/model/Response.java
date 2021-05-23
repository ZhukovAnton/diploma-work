package com.stanum.skrudzh.saltage.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class Response<T> {
    private List<T> data = new ArrayList<>();
    private Meta meta;
    private Error error;

    public Response(T data) {
        this.data = Collections.singletonList(data);
    }

    public Response(List<T> data, Meta meta) {
        this.data = data;
        this.meta = meta;
    }

    public Response(Error error) {
        this.error = error;
    }

    //todo check for duplicates, check meta
    public void merge(Response<T> right) {
        this.data.addAll(right.getData());
        this.meta = right.getMeta();
        this.error = right.getError();
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean hasNext() {
        return meta != null && meta.getNextId() != null;
    }

    @ToString
    @Data
    public static class Meta {
        @JsonProperty("next_id")
        private String nextId;
        @JsonProperty("next_page")
        private String nextPage;
    }

    @ToString
    @Data
    public static class Error {
        private String message;
        @JsonProperty("documentation_url")
        private String documentationUrl;
        @JsonProperty("class")
        private String code;
    }
}
