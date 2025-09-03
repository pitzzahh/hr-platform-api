package dev.araopj.hrplatformapi.audit.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import dev.araopj.hrplatformapi.audit.model.AuditAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Sealed interface for audit request DTOs.
 * <p>
 * Defines the contract for audit request data, ensuring required fields are provided
 * for creating audit records.
 *
 * @see AuditRequest
 * @see AuditRequest.WithChanges
 * @see AuditRequest.WithoutChanges
 * @see AuditAction
 * @see JsonNode
 */
public sealed interface IAuditRequest permits AuditRequest, AuditRequest.WithChanges, AuditRequest.WithoutChanges {
    /**
     * The type of entity being audited (e.g., SalaryGrade, SalaryData).
     */
    @NotBlank(message = "Entity type must not be blank")
    String entityType();

    /**
     * The action performed on the entity (e.g., CREATE, VIEW).
     */
    @NotNull(message = "Audit action must not be null")
    AuditAction action();

    /**
     * The unique identifier of the entity being audited.
     */
    @NotBlank(message = "Entity ID must not be blank")
    String entityId();

    /**
     * The state of the entity before the action (optional).
     */
    JsonNode oldData();

    /**
     * The state of the entity after the action.
     */
    @NotNull(message = "New data must not be null")
    JsonNode newData();

    /**
     * The changes made to the entity (optional).
     */
    JsonNode changes();

    /**
     * The user or system that performed the action.
     */
    @NotBlank(message = "Performed by must not be blank")
    String performedBy();
}