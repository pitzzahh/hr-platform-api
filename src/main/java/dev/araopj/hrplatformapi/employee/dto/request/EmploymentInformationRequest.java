package dev.araopj.hrplatformapi.employee.dto.request;

import dev.araopj.hrplatformapi.employee.model.EmploymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record EmploymentInformationRequest(
        @NotNull
        @NotBlank
        String employeeId,
        @NotNull
        LocalDate startDate,
        LocalDate endDate,
        @NotNull
        EmploymentStatus employmentStatus,
        @NotNull
        @NotBlank
        String sourceOfFund,
        String remarks,
        EmploymentInformationSalaryOverrideRequest employmentInformationSalaryOverrideRequest,
        @Size(min = 1, max = 8, message = "step must be between 1 and 8")
        int step,
        @Size(min = 1, max = 8, message = "anticipatedStep must be between 1 and 8")
        int anticipatedStep,
        @NotNull
        @NotBlank
        String positionId,
        @NotNull
        @NotBlank
        String workplaceId
) {
}
