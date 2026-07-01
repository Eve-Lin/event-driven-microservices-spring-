package com.is.investigator_service.model;

public record Finding(
        Severity severity,
        String title,
        String explanation
) {
}