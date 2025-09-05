package dev.araopj.hrplatformapi.employee.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record PositionResponse(
        String id,
        String code,
        String description,
        EmploymentInformationResponse employmentInformationResponse,
        Instant createdAt,
        Instant updatedAt
) {
}
