package com.is.investigator_service.model;

import java.time.Instant;
import java.util.List;

public record Investigation(

        String serviceName,

        Instant investigatedAt,

        List<Finding> findings

) {
}