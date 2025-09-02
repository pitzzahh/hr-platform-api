package dev.araopj.hrplatformapi.utils;

import dev.araopj.hrplatformapi.audit.dto.AuditDto;
import dev.araopj.hrplatformapi.audit.model.Audit;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationSalaryOverrideResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformationSalaryOverride;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
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
                .performedBy(audit.getPerformedBy())
                .build();
    }

    public static EmploymentInformationSalaryOverrideResponse toDto(EmploymentInformationSalaryOverride override) {
        log.info("Mapping EmploymentInformationSalaryOverride to DTO: {}", override);
        return EmploymentInformationSalaryOverrideResponse.builder()
                .id(override.getId())
                .salary(override.getSalary())
                .effectiveDate(override.getEffectiveDate())
                .createdAt(override.getCreatedAt())
                .updatedAt(override.getUpdatedAt())
                .build();
    }

    public static EmploymentInformationSalaryOverride toEntity(EmploymentInformationSalaryOverrideResponse dto) {
        log.info("Mapping EmploymentInformationSalaryOverrideResponse to entity: {}", dto);
        return EmploymentInformationSalaryOverride.builder()
                .id(dto.id())
                .salary(dto.salary())
                .effectiveDate(dto.effectiveDate())
                .build();
    }

    public static SalaryGradeResponse toDto(SalaryGrade grade) {
        if (grade == null) return null;

        return SalaryGradeResponse.builder()
                .id(grade.getId())
                .legalBasis(grade.getLegalBasis())
                .effectiveDate(grade.getEffectiveDate())
                .tranche(grade.getTranche())
                .salaryGrade(grade.getSalaryGrade())
                .build();
    }

    public static SalaryDataResponse toDto(SalaryData entity, boolean withSalaryGrade) {
        if (entity == null) return null;

        return SalaryDataResponse.builder()
                .id(entity.getId())
                .step(entity.getStep())
                .amount(entity.getAmount())
                .salaryGrade(withSalaryGrade ? entity.getSalaryGrade() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // fallback for existing usages (defaults with parent)
    public static SalaryDataResponse toDto(SalaryData entity) {
        return toDto(entity, true);
    }
}
