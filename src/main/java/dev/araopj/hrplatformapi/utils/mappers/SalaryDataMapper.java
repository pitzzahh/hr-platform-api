package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SalaryDataMapper {

    private final SalaryGradeMapper salaryGradeMapper;

    public SalaryData toEntity(SalaryDataRequest salaryDataRequest, SalaryGrade salaryGrade) {
        if (salaryDataRequest == null) {
            throw new IllegalArgumentException("salaryDataRequest cannot be null");
        }
        return SalaryData.builder()
                .step(salaryDataRequest.step())
                .amount(salaryDataRequest.amount())
                .salaryGrade(salaryGrade)
                .build();
    }

    public SalaryData toEntity(SalaryDataRequest.WithoutSalaryGradeId salaryDataRequest) {
        if (salaryDataRequest == null) {
            throw new IllegalArgumentException("salaryDataRequest cannot be null");
        }
        return SalaryData.builder()
                .step(salaryDataRequest.step())
                .amount(salaryDataRequest.amount())
                .build();
    }

    public SalaryDataResponse toDto(SalaryData entity, SalaryGrade salaryGrade) {
        if (entity == null) {
            throw new IllegalArgumentException("salaryDataRequest cannot be null");
        }
        return SalaryDataResponse.builder()
                .id(entity.getId())
                .step(entity.getStep())
                .amount(entity.getAmount())
                .salaryGrade(salaryGrade != null ? salaryGradeMapper.toDto(salaryGrade, false) : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
