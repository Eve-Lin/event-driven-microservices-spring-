package com.is.investigator_service.model;

import java.time.Instant;

public record ServiceMetrics(
        double requestsPerSecond,
        double errorRate,
        double p95LatencyMs,
        Instant collectedAt
) {}