package dev.araopj.hrplatformapi.salary.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalaryDataRequest {
    @Min(value = 1, message = "Salary step must be at least 1")
    @Max(value = 8, message = "Salary step must be at most 8")
    private byte step;

    @NotNull(message = "Amount cannot be null")
    private Double amount;

    @NotNull(message = "Salary grade ID cannot be null")
    private String salaryGradeId;
}