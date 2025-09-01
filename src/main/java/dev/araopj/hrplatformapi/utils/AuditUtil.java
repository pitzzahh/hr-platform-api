package dev.araopj.hrplatformapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.audit.dto.AuditDto;
import dev.araopj.hrplatformapi.audit.model.AuditAction;
import dev.araopj.hrplatformapi.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Utility class for auditing actions within the application.
 * This class provides methods to log audit events with relevant details.
 */
@Component
@RequiredArgsConstructor
public class AuditUtil {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    /**
     * Logs an audit event with the provided details.
     *
     * @param action     The audit action performed (e.g., {@link AuditAction#CREATE}, {@link AuditAction#UPDATE}, {@link AuditAction#VIEW}).
     * @param entityId   The identifier of the entity being audited.
     * @param oldData    Optional previous state of the entity (before the action), see {@link Optional}.
     * @param newData    The new state of the entity (after the action).
     * @param changes    Optional object representing the changes between old and new data, see {@link Optional}.
     * @param entityType The type of the entity being audited (e.g., "Gsis").
     * @see AuditAction
     * @see AuditDto
     * @see AuditService
     */
    public void audit(AuditAction action, String entityId, Optional<Object> oldData, Object newData, Optional<Object> changes, String entityType) {
        var builder = AuditDto.builder()
                .action(action)
                .newData(objectMapper.valueToTree(newData))
                .performedBy("system")
                .entityType(entityType)
                .entityId(entityId);

        oldData.ifPresent(o -> builder.oldData(objectMapper.valueToTree(o)));
        changes.ifPresent(c -> builder.changes(objectMapper.valueToTree(c)));

        auditService.create(builder.build());
    }

    /**
     * Logs an audit event for an exception that occurred during processing.
     *
     * @param ex            The exception that was thrown.
     * @param message       A custom message describing the error context.
     * @param existingError An optional existing ApiError to augment, see {@link Optional}.
     * @return An ApiError object representing the audited error.
     * @see ApiError
     * @see AuditDto
     * @see AuditService
     */
    public ApiError audit(Exception ex, String message, Optional<ApiError> existingError) {
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
