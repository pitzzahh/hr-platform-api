package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record IdentifierTypeRequest(
        String code,
        String name,
        String description,
        @NotBlank(message = "Identifier ID is required")
        String identifierId
) implements IIdentifierType {
    @Builder
    public record WithoutIdentifierId(
            String code,
            String name,
            String description
    ) implements IIdentifierType {
    }
}
