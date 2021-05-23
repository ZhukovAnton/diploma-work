package com.stanum.skrudzh.controller.system;

import com.stanum.skrudzh.localized_values.LocalizedValuesCache;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Slf4j
public class LocalizedValuesApiController {

    private final LocalizedValuesCache cache;

    @ApiOperation(value = "Reload cache")
    @GetMapping(path = "/localized_values")
    public ResponseEntity<Void> getTransactionableExamples() {
        log.info("Reload cache");
        cache.reloadCache();
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
