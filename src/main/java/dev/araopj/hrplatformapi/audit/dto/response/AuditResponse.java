package dev.araopj.hrplatformapi.audit.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import dev.araopj.hrplatformapi.audit.model.Audit;
import dev.araopj.hrplatformapi.audit.model.AuditAction;
import lombok.Builder;

import java.time.Instant;

/**
 * Record representing the response payload for audit records.
 * <p>
 * This DTO contains the fields returned when retrieving audit records, including
 * audit metadata and timestamps inherited from the {@link Audit} entity.
 *
 * @see Audit
 * @see AuditAction
 * @see JsonNode
 */
@Builder
public record AuditResponse(
        String id,
        String entityType,
        AuditAction action,
        String entityId,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        JsonNode oldData,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        JsonNode newData,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        JsonNode changes,
        String performedBy,
        Instant createdAt,
        Instant updatedAt
) {
}