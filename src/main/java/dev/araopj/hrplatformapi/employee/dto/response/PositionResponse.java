package dev.araopj.hrplatformapi.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

@Builder
public record PositionResponse(
        String id,
        String code,
        String description,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        EmploymentInformationResponse employmentInformationResponse,
        Instant createdAt,
        Instant updatedAt
) {
}
