package dev.araopj.hrplatformapi.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.araopj.hrplatformapi.employee.model.IdDocument;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Builder
public record IdDocumentTypeResponse(
        String id,
        String code,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        IdDocument idDocument
) {
}
