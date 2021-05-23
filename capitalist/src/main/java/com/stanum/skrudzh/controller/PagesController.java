package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.response.WebCredentialsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PagesController {

    @GetMapping(path = "/apple-app-site-association")
    public ResponseEntity<WebCredentialsResponse> appleAppSiteAssociation() {
        return new ResponseEntity<>(new WebCredentialsResponse(), HttpStatus.OK);
    }

    @GetMapping(path = "/.well-known/apple-app-site-association")
    public ResponseEntity<WebCredentialsResponse> wellKnownAppleAppSiteAssociation() {
        return new ResponseEntity<>(new WebCredentialsResponse(), HttpStatus.OK);
    }
}
