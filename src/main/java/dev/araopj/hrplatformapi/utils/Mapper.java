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
        log.debug("Mapping Audit to AuditDto: {}", audit);
        return AuditDto.builder()
                .entityType(audit.getEntityType())
                .action(audit.getAction())
                .entityId(audit.getEntityId())
                .oldData(audit.getOldData())
                .newData(audit.getNewData())
                .changes(audit.getChanges())
                .performedBy(audit.getPerformedBy()).build();
    }

    public SalaryDataResponse toDto(SalaryData salaryData) {
        log.debug("Mapping SalaryData to SalaryDataResponse: {}", salaryData);
        return SalaryDataResponse.builder()
                .id(salaryData.getId())
                .step(salaryData.getStep())
                .amount(salaryData.getAmount())
                .salaryGrade(salaryData.getSalaryGrade())
                .createdAt(salaryData.getCreatedAt())
                .updatedAt(salaryData.getUpdatedAt())
                .build();
    }

}
