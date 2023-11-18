package com.completefarmer.neobank.accountservice.utils;

import com.completefarmer.neobank.accountservice.utils.responses.JsonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class IndexController {

    /**
     * Index route
     * @return ResponseEntity
     */
    @GetMapping("")
    public ResponseEntity<?> index() {
        return JsonResponse.set(HttpStatus.OK, "Request received successfully", Map.of(
                "message", "Welcome to NeoBank Account Service",
                "verion", "1.0.0"
        ));
    }
}
