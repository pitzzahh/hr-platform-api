package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PositionRequest(
        @NotNull(message = "code cannot be null")
        @NotBlank(message = "code cannot be blank")
        String code,
        @NotNull(message = "description cannot be null")
        @NotBlank(message = "description cannot be blank")
        String description,
        @NotNull(message = "employmentInformationId cannot be null")
        @NotBlank(message = "employmentInformationId cannot be blank")
        String employmentInformationId
) {
    @Builder
    public record WithoutEmploymentInformationId(
            @NotNull(message = "code cannot be null")
            @NotBlank(message = "code cannot be blank")
            String code,
            @NotNull(message = "description cannot be null")
            @NotBlank(message = "description cannot be blank")
            String description
    ) {
    }
}
