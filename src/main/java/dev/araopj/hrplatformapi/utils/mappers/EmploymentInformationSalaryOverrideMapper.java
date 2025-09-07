package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationSalaryOverrideRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationSalaryOverrideResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformationSalaryOverride;
import org.springframework.stereotype.Component;

@Component
public class EmploymentInformationSalaryOverrideMapper {
    public EmploymentInformationSalaryOverrideResponse toDto(EmploymentInformationSalaryOverride override) {
        return EmploymentInformationSalaryOverrideResponse.builder()
                .id(override.getId())
                .salary(override.getSalary())
                .effectiveDate(override.getEffectiveDate())
                .createdAt(override.getCreatedAt())
                .updatedAt(override.getUpdatedAt())
                .build();
    }

    public EmploymentInformationSalaryOverride toEntity(EmploymentInformationSalaryOverrideRequest.WithoutEmploymentInformationId employmentInformationSalaryOverrideRequest) {
        return EmploymentInformationSalaryOverride.builder()
                .salary(employmentInformationSalaryOverrideRequest.salary())
                .effectiveDate(employmentInformationSalaryOverrideRequest.effectiveDate())
                .build();
    }

    public EmploymentInformationSalaryOverride toEntity(EmploymentInformationSalaryOverrideResponse informationSalaryOverrideResponse) {
        return EmploymentInformationSalaryOverride.builder()
                .salary(informationSalaryOverrideResponse.salary())
                .effectiveDate(informationSalaryOverrideResponse.effectiveDate())
                .build();
    }
}
