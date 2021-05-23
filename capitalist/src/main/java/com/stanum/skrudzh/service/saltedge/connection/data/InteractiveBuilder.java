package com.stanum.skrudzh.service.saltedge.connection.data;

import com.stanum.skrudzh.controller.form.saltedge.InteractiveParams;
import org.springframework.util.StringUtils;
import java.util.List;

public class InteractiveBuilder {
    private static String req = "{\"data\": {\"credentials\": { %s }  }  }";

    public static String buildReq(List<InteractiveParams> params) {
        String fields = StringUtils.collectionToCommaDelimitedString(params);
        return String.format(req, fields);
    }

}
