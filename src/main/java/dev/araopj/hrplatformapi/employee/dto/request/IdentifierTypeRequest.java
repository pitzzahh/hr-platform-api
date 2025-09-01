package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Builder;

@Builder
public record IdentifierTypeRequest(
        @NotBlank(message = "Code is required")
        String code,
        @NotBlank(message = "Name is required")
        String name,
        @Null
        String description,
        @NotBlank(message = "Identifier ID is required")
        String identifierId
) {
}
