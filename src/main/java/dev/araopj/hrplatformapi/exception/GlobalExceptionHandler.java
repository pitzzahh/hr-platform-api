package dev.araopj.hrplatformapi.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.audit.dto.AuditDto;
import dev.araopj.hrplatformapi.audit.model.AuditAction;
import dev.araopj.hrplatformapi.audit.service.AuditService;
import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

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

    @ExceptionHandler(SalaryGradeNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleSalaryGradeNotFound(SalaryGradeNotFoundException ex) {
        log.warn("Salary grade not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(ApiError.builder()
                        .message("Salary grade not found")
                        .details(List.of(ex.getMessage()))
                        .build()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Error [GENERIC_ERROR]: {}", ex.getMessage(), ex);
        var error = ApiError.builder()
                .message("An unexpected error occurred")
                .details(List.of(ex.getMessage()))
                .build();
        auditService.create(
                AuditDto.builder()
                        .entityType("[GENERIC_ERROR]")
                        .entityId(ex.getMessage())
                        .action(AuditAction.VIEW)
                        .performedBy("system")
                        .newData(objectMapper.valueToTree(error))
                        .build()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.failure(error));
    }

}
