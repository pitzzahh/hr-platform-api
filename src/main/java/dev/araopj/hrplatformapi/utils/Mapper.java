package dev.araopj.hrplatformapi.utils;

import dev.araopj.hrplatformapi.audit.dto.request.AuditRequest;
import dev.araopj.hrplatformapi.audit.dto.response.AuditResponse;
import dev.araopj.hrplatformapi.audit.model.Audit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mapper { // TODO: convert to a component with DI, and split into multiple mappers if needed

    /**
     * Converts an {@link AuditRequest} to an {@link Audit} entity.
     *
     * @param request the audit request DTO (e.g., {@link AuditRequest}, {@link AuditRequest.WithChanges}, {@link AuditRequest.WithoutChanges})
     * @return the corresponding audit entity
     */
    public static Audit toEntity(AuditRequest request) {
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
     * Converts an {@link Audit} entity to an {@link AuditResponse} DTO.
     *
     * @param audit the audit entity
     * @return the corresponding audit response DTO
     */
    public static AuditResponse toDto(Audit audit) {
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
