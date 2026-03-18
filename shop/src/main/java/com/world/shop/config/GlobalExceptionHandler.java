package com.world.shop.config;

import com.world.shop.dto.ErrorResponse;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(RequestNotPermitted ex) {
        return ResponseEntity
                .status(429)
                .body(new ErrorResponse("RATE_LIMIT_EXCEEDED",
                        "Too many requests. Please try again later."));
    }

    @ExceptionHandler(BulkheadFullException.class)
    public ResponseEntity<ErrorResponse> handleBulkheadFull(BulkheadFullException ex) {
        return ResponseEntity
                .status(429)
                .body(new ErrorResponse("TOO_MANY_CONCURRENT_REQUESTS",
                        "System is busy. Please retry."));
    }
}