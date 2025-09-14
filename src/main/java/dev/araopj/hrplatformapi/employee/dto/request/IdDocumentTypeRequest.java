package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record IdDocumentTypeRequest(
        @NotBlank(message = "code is required")
        @NotNull(message = "code cannot be null")
        String code,
        @NotBlank(message = "name is required")
        @NotNull(message = "name cannot be null")
        String name,
        @NotNull(message = "description cannot be null")
        @NotBlank(message = "description is required")
        String description,
        @NotNull(message = "category cannot be null")
        @NotBlank(message = "category is required")
        String category,
        @NotBlank(message = "identifierId is required")
        String identifierId
) {

}
