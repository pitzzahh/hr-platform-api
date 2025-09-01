package dev.araopj.hrplatformapi.exception;

import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.AuditUtil;
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

import java.util.Optional;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AuditUtil auditUtil;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(StandardApiResponse.failure(
                        auditUtil.audit(
                                ex,
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
                        auditUtil.audit(
                                ex,
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
                                auditUtil.audit(
                                        ex,
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
                                auditUtil.audit(
                                        ex,
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
                                auditUtil.audit(
                                        ex,
                                        "Employment information not found",
                                        Optional.empty())
                        )
                );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleNotFound(NotFoundException ex) {
        log.warn("{} not found: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(StandardApiResponse.failure(
                                auditUtil.audit(
                                        ex,
                                        ex.getMessage(),
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
                        auditUtil.audit(
                                ex,
                                ex.getMessage(),
                                Optional.empty()
                        )
                ));

    }
}

