package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.audit.dto.request.AuditRequest;
import dev.araopj.hrplatformapi.audit.dto.response.AuditResponse;
import dev.araopj.hrplatformapi.audit.model.Audit;
import org.springframework.stereotype.Component;

@Component
public class AuditMapper {

    /**
     * Converts an {@link AuditRequest} to an {@link Audit} entity.
     *
     * @param request the audit request DTO
     * @return the corresponding audit entity
     */
    public Audit toEntity(AuditRequest request) {
        return Audit.builder()
                .entityType(request.entityType())
                .action(request.action())
                .entityId(request.entityId())
                .oldData(request.oldData())
                .newData(request.newData())
                .changes(request.changes())
                .performedBy(request.performedBy())
                .build();
    }

    /**
     * Converts an {@link AuditRequest.WithChanges} to an {@link Audit} entity.
     *
     * @param request the audit request DTO with changes
     * @return the corresponding audit entity
     */
    public Audit toEntity(AuditRequest.WithChanges request) {
        return Audit.builder()
                .entityType(request.entityType())
                .action(request.action())
                .entityId(request.entityId())
                .oldData(request.oldData())
                .newData(request.newData())
                .changes(request.changes())
                .performedBy(request.performedBy())
                .build();
    }

    /**
     * Converts an {@link AuditRequest.WithoutChanges} to an {@link Audit} entity.
     *
     * @param request the audit request DTO without changes
     * @return the corresponding audit entity
     */
    public Audit toEntity(AuditRequest.WithoutChanges request) {
        return Audit.builder()
                .entityType(request.entityType())
                .action(request.action())
                .entityId(request.entityId())
                .oldData(request.oldData())
                .newData(request.newData())
                .performedBy(request.performedBy())
                .build();
    }

    /**
     * Converts an {@link Audit} entity to an {@link AuditResponse} DTO.
     *
     * @param audit the audit entity
     * @return the corresponding audit response DTO
     */
    public AuditResponse toDto(Audit audit) {
        return AuditResponse.builder()
                .id(audit.getId())
                .entityType(audit.getEntityType())
                .action(audit.getAction())
                .entityId(audit.getEntityId())
                .oldData(audit.getOldData())
                .newData(audit.getNewData())
                .changes(audit.getChanges())
                .performedBy(audit.getPerformedBy())
                .createdAt(audit.getCreatedAt())
                .updatedAt(audit.getUpdatedAt())
                .build();
    }
}