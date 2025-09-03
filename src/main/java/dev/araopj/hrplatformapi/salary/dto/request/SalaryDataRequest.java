package dev.araopj.hrplatformapi.salary.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SalaryDataRequest(
        @Min(value = 1, message = "step must be at least 1")
        @Max(value = 8, message = "step must be at most 8")
        @NotNull(message = "step is required")
        int step,
        @NotNull(message = "amount is required")
        double amount,
        @NotBlank(message = "salaryGradeId is required")
        String salaryGradeId
) {
    @Builder
    public record WithoutSalaryGradeId(
            @Min(value = 1, message = "step must be at least 1")
            @Max(value = 8, message = "step must be at most 8")
            @NotNull(message = "step is required")
            int step,
            @NotNull(message = "amount is required")
            double amount
    ) {
    }
}