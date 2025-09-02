package dev.araopj.hrplatformapi.salary.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record SalaryGradeRequest(
        @NotNull(message = "Legal basis cannot be null")
        String legalBasis,
        byte tranche,
        @NotNull(message = "Effective date cannot be null")
        LocalDate effectiveDate,
        @Min(value = 1, message = "Salary grade must be at least 1")
        @Max(value = 33, message = "Salary grade must be at most 33")
        byte salaryGrade,
        List<SalaryDataRequest.WithoutSalaryGradeId> salaryData
) {
}