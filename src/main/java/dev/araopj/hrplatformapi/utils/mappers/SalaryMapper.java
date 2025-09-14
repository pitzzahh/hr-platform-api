package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.employee.dto.request.SalaryRequest;
import dev.araopj.hrplatformapi.employee.dto.response.SalaryResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import dev.araopj.hrplatformapi.employee.model.Salary;
import lombok.experimental.UtilityClass;

/**
 * Utility class for mapping between Salary entities and DTOs.
 */
@UtilityClass
public class SalaryMapper {

    public Salary toEntity(SalaryRequest salaryRequest, EmploymentInformation employmentInformation) {
        if (salaryRequest == null) {
            throw new IllegalArgumentException("salaryRequest cannot be null");
        }
        return Salary.builder()
                .amount(salaryRequest.amount())
                .currency(salaryRequest.currency())
                .employmentInformation(employmentInformation)
                .build();
    }

    public Salary toEntity(SalaryRequest salaryRequest) {
        if (salaryRequest == null) {
            throw new IllegalArgumentException("salaryRequest cannot be null");
        }
        return Salary.builder()
                .amount(salaryRequest.amount())
                .currency(salaryRequest.currency())
                .build();
    }

    public Salary toEntity(SalaryResponse salaryResponse) {
        if (salaryResponse == null) {
            throw new IllegalArgumentException("salaryResponse cannot be null");
        }
        return Salary.builder()
                .id(salaryResponse.id())
                .amount(salaryResponse.amount())
                .currency(salaryResponse.currency())
                .build();
    }

    public SalaryResponse toDto(Salary salary) {
        if (salary == null) {
            throw new IllegalArgumentException("salary cannot be null");
        }
        return SalaryResponse.builder()
                .id(salary.getId())
                .amount(salary.getAmount())
                .currency(salary.getCurrency())
                .createdAt(salary.getCreatedAt())
                .updatedAt(salary.getUpdatedAt())
                .build();
    }
}
