package dev.araopj.hrplatformapi.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

@Builder
public record SalaryResponse(
        String id,
        double amount,
        String currency,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        EmploymentInformationResponse employmentInformationResponse,
        Instant createdAt,
        Instant updatedAt
) {
}
