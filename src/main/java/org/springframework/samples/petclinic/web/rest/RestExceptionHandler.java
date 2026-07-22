package org.springframework.samples.petclinic.web.rest;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.samples.petclinic.service.OwnerNotFoundException;
import org.springframework.samples.petclinic.service.PetNotFoundException;
import org.springframework.samples.petclinic.web.rest.dto.ApiErrorDTO;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates the exceptions of the REST channel into JSON responses. It is scoped to the <code>web.rest</code>
 * package so the legacy JSP channel keeps resolving its exceptions through the SimpleMappingExceptionResolver
 * declared in <code>mvc-core-config.xml</code>.
 */
@RestControllerAdvice(basePackages = "org.springframework.samples.petclinic.web.rest")
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ApiErrorDTO body = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), "Bad Request",
            "Los datos del propietario no son válidos", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDTO> handleUnreadableBody(HttpMessageNotReadableException ex) {
        ApiErrorDTO body = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), "Bad Request",
            "El cuerpo de la solicitud no es un JSON válido");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({ OwnerNotFoundException.class, PetNotFoundException.class })
    public ResponseEntity<ApiErrorDTO> handleNotFound(RuntimeException ex) {
        ApiErrorDTO body = new ApiErrorDTO(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

}
