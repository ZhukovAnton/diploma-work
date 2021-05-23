package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.form.saltedge.callback.error.ErrorCallback;
import com.stanum.skrudzh.controller.form.saltedge.callback.interactive.InteractiveCallback;
import com.stanum.skrudzh.controller.form.saltedge.callback.notify.NotifyCallback;
import com.stanum.skrudzh.service.saltedge.callback.ErrorCallbackService;
import com.stanum.skrudzh.service.saltedge.callback.InteractiveCallbackService;
import com.stanum.skrudzh.service.saltedge.callback.NotifyCallbackService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SaltedgeCallbackController {

    @Autowired
    private NotifyCallbackService notifyCallbackService;

    @Autowired
    private InteractiveCallbackService interactiveCallbackService;

    @Autowired
    private ErrorCallbackService errorCallbackService;

    @ApiOperation(value = "Process SaltEdge notify callback")
    @PostMapping(path = "/notify")
    public ResponseEntity<Void> notifyCallback(@ApiParam(value = "") @RequestBody NotifyCallback callback) {
        log.info("Notify callback: {}", callback);
        notifyCallbackService.processCallback(callback);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiOperation(value = "Process SaltEdge interactive callback")
    @PostMapping(path = "/interactive")
    public ResponseEntity<Void> interactiveCallback(@ApiParam(value = "") @RequestBody InteractiveCallback callback) {
        log.info("Interactive callback {}", callback);
        interactiveCallbackService.processCallback(callback);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Process SaltEdge error callback")
    @PostMapping(path = "/fail")
    public ResponseEntity<Void> errorCallback(@ApiParam(value = "") @RequestBody ErrorCallback callback) {
        log.error("Error callback: {}", callback);
        errorCallbackService.processCallback(callback);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
