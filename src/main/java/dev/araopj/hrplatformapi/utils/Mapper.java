package dev.araopj.hrplatformapi.utils;

import dev.araopj.hrplatformapi.audit.dto.request.AuditRequest;
import dev.araopj.hrplatformapi.audit.dto.response.AuditResponse;
import dev.araopj.hrplatformapi.audit.model.Audit;
import dev.araopj.hrplatformapi.employee.dto.request.DivisionStationPlaceOfAssignmentRequest;
import dev.araopj.hrplatformapi.employee.dto.response.DivisionStationPlaceOfAssignmentResponse;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationSalaryOverrideResponse;
import dev.araopj.hrplatformapi.employee.model.DivisionStationPlaceOfAssignment;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformationSalaryOverride;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mapper {
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

    public static SalaryData toEntity(SalaryDataRequest salaryDataRequest) {
        if (salaryDataRequest == null) return null;

        return SalaryData.builder()
                .step(salaryDataRequest.getStep())
                .amount(salaryDataRequest.getAmount())
                .build();
    }

    public static DivisionStationPlaceOfAssignment toEntity(DivisionStationPlaceOfAssignmentRequest divisionStationPlaceOfAssignmentRequest) {
        if (divisionStationPlaceOfAssignmentRequest == null) return null;

        return DivisionStationPlaceOfAssignment.builder()
                .code(divisionStationPlaceOfAssignmentRequest.code())
                .name(divisionStationPlaceOfAssignmentRequest.name())
                .shortName(divisionStationPlaceOfAssignmentRequest.shortName())
                .build();
    }

    public static SalaryGrade toEntity(SalaryGradeRequest salaryGradeRequest) {
        if (salaryGradeRequest == null) return null;

        return SalaryGrade.builder()
                .legalBasis(salaryGradeRequest.legalBasis())
                .effectiveDate(salaryGradeRequest.effectiveDate())
                .tranche(salaryGradeRequest.tranche())
                .salaryGrade(salaryGradeRequest.salaryGrade())
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

    public static DivisionStationPlaceOfAssignmentResponse toDto(DivisionStationPlaceOfAssignment entity, boolean includeEmployee) {
        if (entity == null) return null;

        return DivisionStationPlaceOfAssignmentResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .shortName(entity.getShortName())
                .employmentInformation(includeEmployee ? entity.getEmploymentInformation() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
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

    public static DivisionStationPlaceOfAssignmentResponse toDto(DivisionStationPlaceOfAssignment entity) {
        return toDto(entity, true);
    }

}
