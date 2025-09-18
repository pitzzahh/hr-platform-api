package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SalaryRequest(
        @Min(value = 100, message = "amount must be greater than or equal to 100")
        double amount,
        @NotNull(message = "currency cannot be null")
        @NotBlank(message = "currency cannot be blank")
        String currency,
        @NotNull(message = "employmentInformationId cannot be null")
        @NotBlank(message = "employmentInformationId cannot be blank")
        String employmentInformationId
) {
}
