package dev.araopj.hrplatformapi.utils;

import dev.araopj.hrplatformapi.audit.dto.AuditDto;
import dev.araopj.hrplatformapi.audit.model.Audit;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class Mapper {
    public AuditDto toDto(Audit audit) {
        var builder = AuditDto.builder()
                .entityType(audit.getEntityType())
                .action(audit.getAction())
                .entityId(audit.getEntityId())
                .performedBy(audit.getPerformedBy());
        if (audit.getOldData() != null) {
            builder.oldData(audit.getOldData());
        }
        if (audit.getNewData() != null) {
            builder.newData(audit.getNewData());
        }
        if (audit.getChanges() != null) {
            builder.changes(audit.getChanges());
        }
        log.debug("Sample build: {}", builder.build());
        return builder.build();
    }
}
