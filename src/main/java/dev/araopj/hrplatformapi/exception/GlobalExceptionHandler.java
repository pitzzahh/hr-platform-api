package dev.araopj.hrplatformapi.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.audit.dto.AuditDto;
import dev.araopj.hrplatformapi.audit.model.AuditAction;
import dev.araopj.hrplatformapi.audit.service.AuditService;
import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.StandardApiResponse;
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
import java.util.Optional;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(StandardApiResponse.failure(
                        audit(
                                auditService,
                                ex,
                                objectMapper,
                                "Validation failed",
                                Optional.of(ApiError.builder()
                                        .message("Validation failed")
                                        .details(ex.getBindingResult()
                                                .getFieldErrors()
                                                .stream()
                                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                                .toList())
                                        .build()
                                )
                        )
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleConstraintViolations(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(StandardApiResponse.failure(
                        audit(
                                auditService,
                                ex,
                                objectMapper,
                                "Constraint validation failed",
                                Optional.of(ApiError.builder()
                                        .message("Constraint validation failed")
                                        .details(ex.getConstraintViolations()
                                                .stream()
                                                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                                                .toList())
                                        .build())
                        )
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        var errorMessage = "Database error: Unable to save data due to constraint violation";
        // Optionally extract more details from the root cause (e.g., PSQLException)
        var detailedMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        return ResponseEntity
                .badRequest()
                .body(StandardApiResponse.failure(
                                audit(
                                        auditService,
                                        ex,
                                        objectMapper,
                                        errorMessage + " - " + detailedMessage,
                                        Optional.empty())
                        )
                );
    }

    @ExceptionHandler(SalaryGradeNotFoundException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleSalaryGradeNotFound(SalaryGradeNotFoundException ex) {
        log.warn("Salary grade not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(StandardApiResponse.failure(
                                audit(
                                        auditService,
                                        ex,
                                        objectMapper,
                                        "Salary grade not found",
                                        Optional.empty())
                        )
                );
    }

    @ExceptionHandler(EmploymentInformationNotFoundException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleEmploymentInformationNotFound(EmploymentInformationNotFoundException ex) {
        log.warn("Employment information not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(StandardApiResponse.failure(
                                audit(
                                        auditService,
                                        ex,
                                        objectMapper,
                                        "Employment information not found",
                                        Optional.empty())
                        )
                );
    }

    @ExceptionHandler(GsisNotFoundException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleGsisNotFound(GsisNotFoundException ex) {
        log.warn("GSIS record not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(StandardApiResponse.failure(
                                audit(
                                        auditService,
                                        ex,
                                        objectMapper,
                                        "GSIS record not found",
                                        Optional.empty())
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleGenericException(Exception ex) {
        log.error("Error [GENERIC_ERROR]: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(StandardApiResponse.failure(
                                audit(
                                        auditService,
                                        ex,
                                        objectMapper,
                                        "An unexpected error occurred",
                                        Optional.empty())
                        )
                );
    }

    /**
     * Helper method to create an audit record for exceptions.
     *
     * @param auditService the audit service to use
     * @param ex           the exception that occurred
     * @param objectMapper the object mapper for JSON conversion
     * @param message      the error message
     * @return the created ApiError
     */
    private static ApiError audit(AuditService auditService, Exception ex, ObjectMapper objectMapper, String message, Optional<ApiError> existingError) {
        var error = existingError.orElse(ApiError.builder()
                .message(message)
                .details(List.of(ex.getMessage()))
                .build());
        auditService.create(
                AuditDto.builder()
                        .entityType(ex.getClass().getTypeName())
                        .entityId(ex.getClass().getPackageName())
                        .action(AuditAction.ERROR)
                        .performedBy("system")
                        .newData(objectMapper.valueToTree(error))
                        .build()
        );
        return error;
    }
}
