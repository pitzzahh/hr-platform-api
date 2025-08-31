package dev.araopj.hrplatformapi.employee.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record EmploymentInformationSalaryOverrideResponse(
        String id,
        double salary,
        LocalDate effectiveDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
