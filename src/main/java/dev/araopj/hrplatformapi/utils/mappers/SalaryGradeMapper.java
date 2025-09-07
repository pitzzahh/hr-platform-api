package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import org.springframework.stereotype.Component;

@Component
public class SalaryGradeMapper {
    public SalaryGrade toEntity(SalaryGradeRequest salaryGradeRequest) {
        if (salaryGradeRequest == null) return null;

        return SalaryGrade.builder()
                .legalBasis(salaryGradeRequest.legalBasis())
                .effectiveDate(salaryGradeRequest.effectiveDate())
                .tranche(salaryGradeRequest.tranche())
                .salaryGrade(salaryGradeRequest.salaryGrade())
                .build();
    }

    public SalaryGrade toEntity(SalaryGradeResponse salaryGradeResponse) {
        if (salaryGradeResponse == null) return null;

        return SalaryGrade.builder()
                .legalBasis(salaryGradeResponse.legalBasis())
                .effectiveDate(salaryGradeResponse.effectiveDate())
                .tranche(salaryGradeResponse.tranche())
                .salaryGrade(salaryGradeResponse.salaryGrade())
                .build();
    }

    public SalaryGradeResponse toDto(SalaryGrade grade) {
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

    public SalaryDataResponse toDto(SalaryData entity, boolean withSalaryGrade) {
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

    public SalaryGradeResponse toDto(SalaryGrade salaryGrade, boolean includeSalaryData) {
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
}
