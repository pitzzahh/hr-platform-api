package dev.araopj.hrplatformapi.salary.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryDataRequest {

    @Min(value = 1, message = "Salary step must be at least 1")
    @Max(value = 8, message = "Salary step must be at most 8")
    private int step;

    @NotNull(message = "Amount cannot be null")
    private double amount;

    private String salaryGradeId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WithoutSalaryGradeId {

        @Min(value = 1, message = "Salary step must be at least 1")
        @Max(value = 8, message = "Salary step must be at most 8")
        private int step;

        @NotNull(message = "Amount cannot be null")
        private double amount;
    }
}