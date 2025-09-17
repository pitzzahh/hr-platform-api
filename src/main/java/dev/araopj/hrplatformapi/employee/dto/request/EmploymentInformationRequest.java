package dev.araopj.hrplatformapi.employee.dto.request;

import dev.araopj.hrplatformapi.employee.model.EmploymentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record EmploymentInformationRequest(
        @NotNull(message = "employeeId cannot be null")
        @NotBlank(message = "employeeId cannot be blank")
        String employeeId,
        @NotNull(message = "startDate cannot be null")
        LocalDate startDate,
        LocalDate endDate,
        @NotNull(message = "employmentStatus cannot be null")
        EmploymentStatus employmentStatus,
        @NotNull(message = "sourceOfFund cannot be null")
        @NotBlank(message = "sourceOfFund cannot be blank")
        String sourceOfFund,
        String remarks,
        String employmentInformationSalaryOverrideId,
        @NotNull(message = "positionId cannot be null")
        @NotBlank(message = "positionId cannot be blank")
        String positionId,
        @NotNull(message = "workplaceId cannot be null")
        @NotBlank(message = "workplaceId cannot be blank")
        String workplaceId,
        @NotNull(message = "salaryId cannot be null")
        @NotBlank(message = "salaryId cannot be blank")
        String salaryId
) {
}
