package dev.araopj.hrplatformapi.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.araopj.hrplatformapi.employee.model.Employee;
import lombok.Builder;

import java.time.Instant;

@Builder
public record DivisionStationPlaceOfAssignmentResponse(
        String id,
        String code,
        String name,
        String shortName,
        Instant createdAt,
        Instant updatedAt,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Employee employee
) {
}

