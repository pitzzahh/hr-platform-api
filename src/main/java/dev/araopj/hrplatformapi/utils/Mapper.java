package dev.araopj.hrplatformapi.utils;

import dev.araopj.hrplatformapi.audit.dto.AuditDto;
import dev.araopj.hrplatformapi.audit.model.Audit;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
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

    public SalaryDataResponse toDto(SalaryData salaryData) {
        return SalaryDataResponse.builder()
                .id(salaryData.getId())
                .step(salaryData.getStep())
                .amount(salaryData.getAmount())
                .salaryGradeId(salaryData.getSalaryGrade() != null ? salaryData.getSalaryGrade().getId() : null)
                .createdAt(salaryData.getCreatedAt())
                .updatedAt(salaryData.getUpdatedAt())
                .build();
    }

}
