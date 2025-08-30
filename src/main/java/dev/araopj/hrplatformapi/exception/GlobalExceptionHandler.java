package dev.araopj.hrplatformapi.exception;

import dev.araopj.hrplatformapi.dto.ApiError;
import dev.araopj.hrplatformapi.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(ApiError.builder()
                        .message("Validation failed")
                        .details(ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .toList())
                        .build()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolations(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(ApiError.builder()
                        .message("Constraint validation failed")
                        .details(ex.getConstraintViolations()
                                .stream()
                                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                                .toList())
                        .build()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        var errorMessage = "Database error: Unable to save data due to constraint violation";

        // Optionally extract more details from the root cause (e.g., PSQLException)
        var detailedMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(ApiError.builder()
                        .message(errorMessage)
                        .details(List.of(detailedMessage))
                        .build()));
    }
}
