package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record WorkplaceRequest(
        @NotBlank(message = "code cannot be blank")
        @NotNull(message = "code cannot be null")
        String code,
        @NotBlank(message = "name cannot be blank")
        @NotNull(message = "name cannot be null")
        String name,
        String shortName,
        @NotBlank(message = "employmentInformationId cannot be blank")
        @NotNull(message = "employmentInformationId cannot be null")
        String employmentInformationId
) {
}
