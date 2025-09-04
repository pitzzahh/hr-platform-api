package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record IdentifierTypeRequest(
        @NotBlank(message = "Code is required")
        String code,
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Description is required")
        String description,
        @NotBlank(message = "Identifier ID is required")
        String identifierId
) {
    @Builder
    public record WithoutIdentifierId(
            @NotBlank(message = "Code is required")
            String code,
            @NotBlank(message = "Name is required")
            String name,
            @NotBlank(message = "Description is required")
            String description
    ) {
    }
}
