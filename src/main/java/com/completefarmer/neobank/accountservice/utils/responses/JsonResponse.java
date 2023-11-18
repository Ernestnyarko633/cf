package com.completefarmer.neobank.accountservice.utils.responses;

import org.apache.commons.lang3.Range;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Response parser
 * Converts responses to ResponseEntity
 */
public final class JsonResponse {
    public static ResponseEntity<Object> set (HttpStatus httpStatus, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("success", Range.between(HttpStatus.OK.value(), 299).contains(httpStatus.value()));
        response.put("data", data);
        response.put("httpStatusCode", httpStatus.value());
        return new ResponseEntity<>(response, httpStatus);
    }
}
