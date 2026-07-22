package org.springframework.samples.petclinic.web.rest.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Error payload of the owner REST API. When a request is rejected by validation, <code>errors</code> identifies the
 * offending fields.
 */
public class ApiErrorDTO {

    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String error;
    private String message;
    private Map<String, String> errors;

    public ApiErrorDTO(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ApiErrorDTO(int status, String error, String message, Map<String, String> errors) {
        this(status, error, message);
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public int getStatus() {
        return this.status;
    }

    public String getError() {
        return this.error;
    }

    public String getMessage() {
        return this.message;
    }

    public Map<String, String> getErrors() {
        return this.errors;
    }

}
