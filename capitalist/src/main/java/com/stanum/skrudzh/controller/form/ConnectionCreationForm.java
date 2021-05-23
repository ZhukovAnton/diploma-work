package com.stanum.skrudzh.controller.form;

import com.stanum.skrudzh.controller.form.saltedge.SaltedgeSession;
import com.stanum.skrudzh.model.enums.ProviderStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ConnectionCreationForm {
    private ConnectionCF connection;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConnectionCF {

        @ApiModelProperty(value = "connection saltEdge id")
        private String saltEdgeConnectionId;

        @ApiModelProperty(value = "provider saltEdge id")
        private String providerId = null;

        @ApiModelProperty
        private String providerCode = null;

        @ApiModelProperty
        private String providerName = null;

        @ApiModelProperty
        private String logoUrl = null;

        @ApiModelProperty
        private ProviderStatusEnum status = null;

        @ApiModelProperty
        private SaltedgeSession saltedgeConnectionSession = null;

    }
}
