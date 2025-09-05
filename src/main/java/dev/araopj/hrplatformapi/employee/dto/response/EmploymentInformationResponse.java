package dev.araopj.hrplatformapi.employee.dto.response;

import dev.araopj.hrplatformapi.employee.model.EmploymentStatus;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record EmploymentInformationResponse(
        String id,
        EmployeeResponse employeeResponse,
        LocalDate startDate,
        LocalDate endDate,
        EmploymentStatus employmentStatus,
        String sourceOfFund,
        String remarks,
        EmploymentInformationSalaryOverrideResponse employmentInformationSalaryOverrideResponse,
        int step,
        int anticipatedStep,
        PositionResponse positionResponse,
        WorkplaceResponse workplaceResponse

) {
}
