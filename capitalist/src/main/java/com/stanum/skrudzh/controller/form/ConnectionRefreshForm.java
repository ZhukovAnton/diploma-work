package com.stanum.skrudzh.controller.form;

import com.stanum.skrudzh.controller.form.saltedge.InteractiveParams;
import com.stanum.skrudzh.controller.form.saltedge.SaltedgeSession;
import lombok.Data;

import java.util.List;

@Data
public class ConnectionRefreshForm {

    private ConnectionRF connection;

    @Data
    public static class ConnectionRF {

        private String saltEdgeConnectionId;

        private boolean refreshOnlyConnectionData;

        private SaltedgeSession saltedgeConnectionSession;

        private List<InteractiveParams> interactiveCredentials;
    }
}
