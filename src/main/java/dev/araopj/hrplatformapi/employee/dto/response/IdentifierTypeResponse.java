package dev.araopj.hrplatformapi.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.araopj.hrplatformapi.employee.model.Identifier;

import java.time.Instant;

public record IdentifierTypeResponse(
        String id,
        String code,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Identifier identifier
) {
}
