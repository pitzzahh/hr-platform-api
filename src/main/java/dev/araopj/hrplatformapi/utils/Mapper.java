package dev.araopj.hrplatformapi.utils;

import dev.araopj.hrplatformapi.audit.dto.AuditDto;
import dev.araopj.hrplatformapi.audit.model.Audit;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationSalaryOverrideResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformationSalaryOverride;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mapper {
    public static AuditDto toDto(Audit audit) {
        log.info("Mapping Audit to AuditDto: {}", audit);
        return AuditDto.builder()
                .entityType(audit.getEntityType())
                .action(audit.getAction())
                .entityId(audit.getEntityId())
                .oldData(audit.getOldData())
                .newData(audit.getNewData())
                .changes(audit.getChanges())
                .performedBy(audit.getPerformedBy()).build();
    }

    public static SalaryDataResponse toDto(SalaryData salaryData) {
        log.info("Mapping SalaryData to SalaryDataResponse: {}", salaryData);
        return SalaryDataResponse.builder()
                .id(salaryData.getId())
                .step(salaryData.getStep())
                .amount(salaryData.getAmount())
                .salaryGrade(salaryData.getSalaryGrade())
                .createdAt(salaryData.getCreatedAt())
                .updatedAt(salaryData.getUpdatedAt())
                .build();
    }

    public static EmploymentInformationSalaryOverrideResponse toDto(EmploymentInformationSalaryOverride employmentInformationSalaryOverride) {
        log.info("Mapping EmploymentInformationSalaryOverrideRequest to EmploymentInformationSalaryOverrideRequest: {}", employmentInformationSalaryOverride);
        return EmploymentInformationSalaryOverrideResponse.builder()
                .id(employmentInformationSalaryOverride.getId())
                .salary(employmentInformationSalaryOverride.getSalary())
                .effectiveDate(employmentInformationSalaryOverride.getEffectiveDate())
                .createdAt(employmentInformationSalaryOverride.getCreatedAt())
                .updatedAt(employmentInformationSalaryOverride.getUpdatedAt())
                .build();
    }

    public static SalaryData toEntity(SalaryDataResponse salaryDataResponse) {
        log.info("Mapping SalaryDataResponse to SalaryData: {}", salaryDataResponse);
        return SalaryData.builder()
                .id(salaryDataResponse.id())
                .step(salaryDataResponse.step())
                .amount(salaryDataResponse.amount())
                .salaryGrade(salaryDataResponse.salaryGrade())
                .build();
    }

    public static EmploymentInformationSalaryOverride toEntity(EmploymentInformationSalaryOverrideResponse employmentInformationSalaryOverrideResponse) {
        log.info("Mapping EmploymentInformationSalaryOverrideResponse to EmploymentInformationSalaryOverride: {}", employmentInformationSalaryOverrideResponse);
        return EmploymentInformationSalaryOverride.builder()
                .id(employmentInformationSalaryOverrideResponse.id())
                .salary(employmentInformationSalaryOverrideResponse.salary())
                .effectiveDate(employmentInformationSalaryOverrideResponse.effectiveDate())
                .build();
    }

}
