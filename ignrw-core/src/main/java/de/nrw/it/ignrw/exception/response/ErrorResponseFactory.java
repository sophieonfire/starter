package de.nrw.it.ignrw.exception.response;

import org.slf4j.MDC;

import java.time.Instant;
import java.util.Map;

public class ErrorResponseFactory {

    public static ErrorResponse build(
            String message,
            ErrorCode errorCode,
            Map<String, String> errors
    ) {
        String traceId = MDC.get("traceId"); // Sleuth / Observability

        return new ErrorResponse(
                message,
                errorCode,
                errors,
                traceId,
                Instant.now()
        );
    }
}
