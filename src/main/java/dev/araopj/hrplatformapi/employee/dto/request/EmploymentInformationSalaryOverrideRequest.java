package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record EmploymentInformationSalaryOverrideRequest(
        @NotNull(message = "Salary cannot be null")
        @Min(value = 1, message = "Salary must be at least 1")
        double salary,
        @NotNull(message = "Effective date cannot be null")
        LocalDate effectiveDate,
        @NotBlank(message = "Employment Information ID cannot be blank")
        String employmentInformationId
) {

    @Builder
    public record WithoutEmploymentInformationId(
            @NotNull(message = "Salary cannot be null")
            @Min(value = 1, message = "Salary must be at least 1")
            double salary,
            @NotNull(message = "Effective date cannot be null")
            LocalDate effectiveDate
    ) {
    }
}
