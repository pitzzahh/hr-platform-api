package dev.araopj.hrplatformapi.employee.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder
public record EmploymentInformationSalaryOverrideResponse(
        String id,
        double salary,
        LocalDate effectiveDate,
        Instant createdAt,
        Instant updatedAt
) {
}
