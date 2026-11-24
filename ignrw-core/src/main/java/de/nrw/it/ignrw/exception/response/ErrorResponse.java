package de.nrw.it.ignrw.exception.response;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        String message,
        ErrorCode errorCode,
        Map<String, String> errors,
        String traceId,
        Instant timestamp
) { }
