package com.stanum.skrudzh.controller;

import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.stanum.skrudzh.service.reminder.NotificationService;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class SystemController {

    @Value("${threebaskets.env}")
    private String systemEnv;

    @Autowired
    private NotificationService notificationService;

    @ApiOperation(value = "Switch apns mode(only for dev environment)")
    @GetMapping(path = "/system/apns/{mode}")
    public ResponseEntity<Void> switchApnsMode(
            @ApiParam(value = "", required = true) @PathVariable("mode") String mode) {
        if(!"development".equals(systemEnv)) {
            log.warn("Environment is not in development mode");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.info("Switch apns client to {} mode", mode);
        if (Constants.ENVIRONMENT_PROD.equals(mode)) {
            notificationService.initClient(ApnsClientBuilder.PRODUCTION_APNS_HOST);
        } else {
            notificationService.initClient(ApnsClientBuilder.DEVELOPMENT_APNS_HOST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
