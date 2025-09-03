package dev.araopj.hrplatformapi.audit.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import dev.araopj.hrplatformapi.audit.model.AuditAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;

/**
 * Record representing the request payload for creating an audit record.
 * <p>
 * This is the base request DTO for audit operations, including all fields.
 * Variants like {@link WithChanges} and {@link WithoutChanges} provide flexibility
 * for specific use cases, such as updates or create/view actions.
 *
 * @see WithChanges
 * @see WithoutChanges
 * @see AuditAction
 * @see JsonNode
 */
@Builder
public record AuditRequest(
        @NotBlank(message = "Entity type must not be blank")
        String entityType,
        @NotNull(message = "Audit action must not be null")
        AuditAction action,
        @NotBlank(message = "Entity ID must not be blank")
        String entityId,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        JsonNode oldData,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @NotNull(message = "New data must not be null")
        JsonNode newData,
        @Null(message = "Changes should be null")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        JsonNode changes,
        @NotBlank(message = "Performed by must not be blank")
        String performedBy
) {

    /**
     * Variant of {@link AuditRequest} that includes changes.
     * <p>
     * Used for audit actions (e.g., UPDATE) where changes between old and new data are tracked.
     */
    @Builder
    public record WithChanges(
            @NotBlank(message = "Entity type must not be blank")
            String entityType,
            @NotNull(message = "Audit action must not be null")
            AuditAction action,
            @NotBlank(message = "Entity ID must not be blank")
            String entityId,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            JsonNode oldData,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @NotNull(message = "New data must not be null")
            JsonNode newData,
            @NotNull(message = "Changes must not be null")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            JsonNode changes,
            @NotBlank(message = "Performed by must not be blank")
            String performedBy
    ) {
    }

    /**
     * Variant of {@link AuditRequest} without changes.
     * <p>
     * Used for audit actions (e.g., CREATE, VIEW) where changes are not applicable.
     */
    @Builder
    public record WithoutChanges(
            @NotBlank(message = "Entity type must not be blank")
            String entityType,
            @NotNull(message = "Audit action must not be null")
            AuditAction action,
            @NotBlank(message = "Entity ID must not be blank")
            String entityId,
            @Null(message = "Old data should be null")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            JsonNode oldData,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @NotNull(message = "New data must not be null")
            JsonNode newData,
            @NotBlank(message = "Performed by must not be blank")
            String performedBy
    ) {
    }
}