package dev.araopj.hrplatformapi.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.araopj.hrplatformapi.employee.model.Identifier;

import java.time.LocalDateTime;

public record IdentifierTypeResponse(
        String id,
        String code,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Identifier identifier
) {
}
