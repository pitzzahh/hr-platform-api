package dev.araopj.hrplatformapi.exception;

import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.StandardApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AuditUtil auditUtil;

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        var body = new LinkedHashMap<String, Object>();
        body.put("timestamp", Instant.now().toString());
        body.put("error", Map.of(
                "message", String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                        ex.getValue(),
                        ex.getName(),
                        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"),
                "details", List.of(ex.getMessage())
        ));
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("""
                \nERROR: Validation Failure
                  TYPE: MethodArgumentNotValidException
                  MESSAGE: {}
                  DETAILS: Validation errors occurred in request""", ex.getMessage());
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

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        log.warn("""
                \nERROR: Validation Failure
                  TYPE: HandlerMethodValidationException
                  MESSAGE: {}
                  DETAILS: Validation errors occurred in method parameters""", ex.getMessage());

        // Extract validation errors
        var details = ex.getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    } else {
                        return error.getDefaultMessage();
                    }
                })
                .collect(Collectors.toList());

        return ResponseEntity
                .badRequest()
                .body(StandardApiResponse.failure(
                        auditUtil.audit(
                                ex,
                                "Validation failed",
                                Optional.of(ApiError.builder()
                                        .message("Validation failed")
                                        .details(details)
                                        .build()
                                )
                        )
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleConstraintViolations(ConstraintViolationException ex) {
        log.warn("""
                \nERROR: Constraint Violation
                  TYPE: ConstraintViolationException
                  MESSAGE: {}
                  DETAILS: Constraint validation errors occurred""", ex.getMessage());
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
        var errorMessage = "Database error: Unable to save data due to constraint violation";
        var detailedMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        log.warn("""
                \nERROR: Data Integrity Violation
                  TYPE: DataIntegrityViolationException
                  MESSAGE: {}
                  DETAILS: {}""", ex.getMessage(), detailedMessage);
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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleNotFound(NotFoundException ex) {
        log.warn("""
                \nERROR: Resource Not Found
                  TYPE: {}
                  MESSAGE: {}
                  DETAILS: Requested resource not found in the system""", ex.getClass().getSimpleName(), ex.getMessage());
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

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleBadRequest(BadRequestException ex) {
        log.warn("""
                \nERROR: Bad Request
                  TYPE: {}
                  MESSAGE: {}
                  DETAILS: Malformed request or invalid parameters""", ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(StandardApiResponse.failure(
                                auditUtil.audit(
                                        ex,
                                        ex.getMessage(),
                                        Optional.empty())
                        )
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("""
                \nERROR: Illegal Argument
                  TYPE: {}
                  MESSAGE: {}
                  DETAILS: An illegal argument was provided to a method""", ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(StandardApiResponse.failure(
                                auditUtil.audit(
                                        ex,
                                        ex.getMessage(),
                                        Optional.empty())
                        )
                );
    }


    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleInvalidRequest(InvalidRequestException ex) {
        log.warn("""
                \nERROR: Invalid Request
                  TYPE: {}
                  MESSAGE: {}
                  DETAILS: Invalid request parameters or data""", ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(StandardApiResponse.failure(
                        auditUtil.audit(
                                ex,
                                ex.getMessage(),
                                Optional.of(ApiError.builder()
                                        .message("Invalid request")
                                        .details(List.of(ex.getMessage()))
                                        .build())
                        )
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardApiResponse<ApiError>> handleGenericException(Exception ex) {
        log.error("""
                \nERROR: Unexpected Server Error
                  TYPE: {}
                  MESSAGE: {}
                  DETAILS: An unexpected error occurred on the server""", ex.getClass().getSimpleName(), ex.getMessage(), ex);
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