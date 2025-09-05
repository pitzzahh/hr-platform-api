package dev.araopj.hrplatformapi.utils;

import dev.araopj.hrplatformapi.audit.dto.request.AuditRequest;
import dev.araopj.hrplatformapi.audit.dto.response.AuditResponse;
import dev.araopj.hrplatformapi.audit.model.Audit;
import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationSalaryOverrideRequest;
import dev.araopj.hrplatformapi.employee.dto.request.IdentifierRequest;
import dev.araopj.hrplatformapi.employee.dto.request.IdentifierTypeRequest;
import dev.araopj.hrplatformapi.employee.dto.request.WorkplaceRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationSalaryOverrideResponse;
import dev.araopj.hrplatformapi.employee.dto.response.IdentifierResponse;
import dev.araopj.hrplatformapi.employee.dto.response.IdentifierTypeResponse;
import dev.araopj.hrplatformapi.employee.dto.response.WorkplaceResponse;
import dev.araopj.hrplatformapi.employee.model.*;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

// TODO: split into multiple entity specific mapper class, technical debt for now
@Slf4j
public class Mapper { // TODO: convert to a component with DI, and split into multiple mappers if needed
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

    public static EmploymentInformationSalaryOverride toEntity(EmploymentInformationSalaryOverrideRequest.WithoutEmploymentInformationId employmentInformationSalaryOverrideRequest) {
        log.info("Mapping EmploymentInformationSalaryOverrideRequest to entity: {}", employmentInformationSalaryOverrideRequest);
        return EmploymentInformationSalaryOverride.builder()
                .salary(employmentInformationSalaryOverrideRequest.salary())
                .effectiveDate(employmentInformationSalaryOverrideRequest.effectiveDate())
                .build();
    }

    public static Workplace toEntity(WorkplaceRequest workplaceRequest, EmploymentInformation employmentInformation) {
        if (workplaceRequest == null) return null;

        return Workplace.builder()
                .code(workplaceRequest.code())
                .name(workplaceRequest.name())
                .shortName(workplaceRequest.shortName())
                .employmentInformation(employmentInformation)
                .build();
    }

    public static Workplace toEntity(WorkplaceRequest.WithoutEmploymentInformationId workplaceRequest) {
        if (workplaceRequest == null) return null;

        return Workplace.builder()
                .code(workplaceRequest.code())
                .name(workplaceRequest.name())
                .shortName(workplaceRequest.shortName())
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

    public static WorkplaceResponse toDto(Workplace entity, boolean includeEmployee) {
        if (entity == null) return null;

        return WorkplaceResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .shortName(entity.getShortName())
                .employmentInformation(includeEmployee ? entity.getEmploymentInformation() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static IdentifierTypeResponse toDto(IdentifierType identifierType) {
        if (identifierType == null) return null;

        return IdentifierTypeResponse.builder()
                .id(identifierType.getId())
                .code(identifierType.getCode())
                .name(identifierType.getName())
                .description(identifierType.getDescription())
                .createdAt(identifierType.getCreatedAt())
                .updatedAt(identifierType.getUpdatedAt())
                .build();
    }

    public static IdentifierResponse toDto(Identifier identifier, boolean includeEmployee) {
        if (identifier == null) return null;

        return IdentifierResponse.builder()
                .id(identifier.getId())
                .identifierNumber(identifier.getIdentifierNumber())
                .type(toDto(identifier.getType()))
                .issuedDate(identifier.getIssuedDate())
                .issuedPlace(identifier.getIssuedPlace())
                .employee(includeEmployee ? identifier.getEmployee() : null)
                .createdAt(identifier.getCreatedAt())
                .updatedAt(identifier.getUpdatedAt())
                .build();
    }

    public static Identifier toEntity(IdentifierRequest dto) {
        if (dto == null) return null;

        return Identifier.builder()
                .identifierNumber(dto.identifierNumber())
                .type(toEntity(dto.identifierTypeRequest()))
                .issuedDate(dto.issuedDate())
                .issuedPlace(dto.issuedPlace())
                .build();
    }

    public static IdentifierType toEntity(@NotNull(message = "identifierTypeRequest cannot be null") @NotBlank(message = "identifierTypeRequest cannot be blank") IdentifierTypeRequest identifierTypeRequest) {
        if (identifierTypeRequest == null) return null;

        return IdentifierType.builder()
                .code(identifierTypeRequest.code())
                .name(identifierTypeRequest.name())
                .description(identifierTypeRequest.description())
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

    public static WorkplaceResponse toDto(Workplace entity) {
        return toDto(entity, true);
    }

    public static IdentifierResponse toDto(Identifier identifier) {
        return toDto(identifier, false);
    }

}
