package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmploymentInformationSalaryOverrideRequest {

    @NotNull(message = "Salary cannot be null")
    @Min(value = 1, message = "Salary must be at least 1")
    private double salary;

    @NotNull(message = "Effective date cannot be null")
    private LocalDate effectiveDate;

    @NotBlank(message = "Employment Information ID cannot be blank")
    private String employmentInformationId;
}
