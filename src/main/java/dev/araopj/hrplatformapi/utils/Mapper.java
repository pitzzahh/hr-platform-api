package dev.araopj.hrplatformapi.utils;

import dev.araopj.hrplatformapi.audit.dto.request.AuditRequest;
import dev.araopj.hrplatformapi.audit.dto.response.AuditResponse;
import dev.araopj.hrplatformapi.audit.model.Audit;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mapper { // TODO: convert to a component with DI, and split into multiple mappers if needed

    public static SalaryGrade toEntity(SalaryGradeRequest salaryGradeRequest) {
        if (salaryGradeRequest == null) return null;

        return SalaryGrade.builder()
                .legalBasis(salaryGradeRequest.legalBasis())
                .effectiveDate(salaryGradeRequest.effectiveDate())
                .tranche(salaryGradeRequest.tranche())
                .salaryGrade(salaryGradeRequest.salaryGrade())
                .build();
    }

    public static SalaryData toEntity(SalaryDataRequest salaryDataRequest, SalaryGrade salaryGrade) {
        if (salaryDataRequest == null) return null;
        return SalaryData.builder()
                .step(salaryDataRequest.step())
                .amount(salaryDataRequest.amount())
                .salaryGrade(salaryGrade)
                .build();
    }

    public static SalaryData toEntity(SalaryDataRequest.WithoutSalaryGradeId salaryDataRequest) {
        if (salaryDataRequest == null) return null;
        return SalaryData.builder()
                .step(salaryDataRequest.step())
                .amount(salaryDataRequest.amount())
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
                .createdAt(grade.getCreatedAt())
                .updatedAt(grade.getUpdatedAt())
                .build();
    }

    public static SalaryDataResponse toDto(SalaryData entity, boolean withSalaryGrade) {
        if (entity == null) return null;

        return SalaryDataResponse.builder()
                .id(entity.getId())
                .step(entity.getStep())
                .amount(entity.getAmount())
                .salaryGrade(withSalaryGrade ? toDto(entity.getSalaryGrade()) : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static SalaryGradeResponse toDto(SalaryGrade salaryGrade, boolean includeSalaryData) {
        if (salaryGrade == null) return null;

        return SalaryGradeResponse.builder()
                .id(salaryGrade.getId())
                .legalBasis(salaryGrade.getLegalBasis())
                .effectiveDate(salaryGrade.getEffectiveDate())
                .tranche(salaryGrade.getTranche())
                .salaryGrade(salaryGrade.getSalaryGrade())
                .createdAt(salaryGrade.getCreatedAt())
                .salaryData(
                        includeSalaryData && salaryGrade.getSalaryData() != null ?
                                salaryGrade.getSalaryData().stream()
                                        .map(sd -> toDto(sd, false)) // Avoid circular reference
                                        .toList() : null
                )
                .updatedAt(salaryGrade.getUpdatedAt()).build();
    }


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

    // fallback for existing usages (defaults with parent)
    public static SalaryDataResponse toDto(SalaryData entity) {
        return toDto(entity, true);
    }

}
