package dev.araopj.hrplatformapi.salary.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record SalaryGradeRequest(
        @NotBlank(message = "Legal basis cannot be blank")
        @NotNull(message = "Legal basis cannot be null")
        String legalBasis,
        int tranche,
        @NotNull(message = "Effective date cannot be null")
        LocalDate effectiveDate,
        @Min(value = 1, message = "Salary grade must be at least 1")
        @Max(value = 33, message = "Salary grade must be at most 33")
        int salaryGrade,
        List<SalaryDataRequest.WithoutSalaryGradeId> salaryData
) {
}