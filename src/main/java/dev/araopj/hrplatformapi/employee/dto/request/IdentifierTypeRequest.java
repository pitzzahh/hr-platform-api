package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record IdentifierTypeRequest(
        @NotBlank(message = "code is required")
        @NotNull(message = "code cannot be null")
        String code,
        @NotBlank(message = "name is required")
        @NotNull(message = "name cannot be null")
        String name,
        @NotBlank(message = "description is required")
        String description,
        @NotBlank(message = "identifierId is required")
        String identifierId
) {
    @Builder
    public record WithoutIdentifierId(
            @NotBlank(message = "Code is required")
            @NotNull(message = "code cannot be null")
            String code,
            @NotBlank(message = "name is required")
            @NotNull(message = "name cannot be null")
            String name,
            @NotBlank(message = "description is required")
            String description
    ) {
    }
}
