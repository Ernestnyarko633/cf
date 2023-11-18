package com.completefarmer.neobank.accountservice.utils.exceptions;

import com.completefarmer.neobank.accountservice.utils.responses.JsonResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Custom exception handler class
 * @author appiersign
 */

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidArgument(MethodArgumentNotValidException exception) {
        Map<String, String> errorMap = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });

        return JsonResponse.set(HttpStatus.BAD_REQUEST, "Validation failed.", errorMap);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleInvalidArgument(DataIntegrityViolationException exception) {
        return JsonResponse.set(HttpStatus.INTERNAL_SERVER_ERROR, "Data integrity violation: ", "Data persistence error, please try again later");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleInvalidArgument(NoSuchElementException exception) {
        return JsonResponse.set(HttpStatus.NOT_FOUND, "Internal server error", "Entity does not exist");
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<?> handleInsufficientBalanceException(InsufficientBalanceException exception) {
        return JsonResponse.set(HttpStatus.BAD_REQUEST, "Insufficient Balance", "Account balance is low");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleInvalidArgument(RuntimeException exception) {
        return JsonResponse.set(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "Something went wrong, please try again later");
    }

    @ExceptionHandler(TransactionCannotBeCancelledException.class)
    public ResponseEntity<?> handleInvalidArgument(TransactionCannotBeCancelledException exception) {
        return JsonResponse.set(HttpStatus.BAD_REQUEST, exception.getMessage(), exception.getMessage());
    }

    @ExceptionHandler(BadTransactionCandidateException.class)
    public ResponseEntity<?> handleBadTransactionCandidateException(BadTransactionCandidateException exception) {
        return JsonResponse.set(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(BadTransactionStatusException.class)
    public ResponseEntity<?> handleBadTransactionStatusException(BadTransactionStatusException exception) {
        return JsonResponse.set(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
    }
}
