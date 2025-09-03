package dev.araopj.hrplatformapi.salary.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SalaryDataRequest(
        byte step,
        double amount,
        @NotBlank(message = "Salary grade ID is required")
        String salaryGradeId
) implements ISalaryData {
    @Builder
    public record WithoutSalaryGradeId(
            byte step,
            double amount
    ) implements ISalaryData {
    }
}