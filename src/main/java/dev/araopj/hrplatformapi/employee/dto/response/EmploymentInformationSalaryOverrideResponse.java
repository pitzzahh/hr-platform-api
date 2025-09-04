package dev.araopj.hrplatformapi.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder
public record EmploymentInformationSalaryOverrideResponse(
        String id,
        double salary,
        LocalDate effectiveDate,
        Instant createdAt,
        Instant updatedAt,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        EmploymentInformation employmentInformation
) {
}
