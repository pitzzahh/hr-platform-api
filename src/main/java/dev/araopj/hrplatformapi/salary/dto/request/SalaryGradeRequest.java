package dev.araopj.hrplatformapi.salary.dto.request;

import dev.araopj.hrplatformapi.salary.model.SalaryData;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;

@Data
public class SalaryGradeRequest {
    @NotNull(message = "Legal basis cannot be null")
    private String legalBasis;

    @Null
    private byte tranche;

    @NotNull(message = "Effective date cannot be null")
    private LocalDate effectiveDate;

    @Min(value = 1, message = "Salary grade must be at least 1")
    @Max(value = 33, message = "Salary grade must be at most 33")
    private byte salaryGrade;

    @Null
    private ArrayList<SalaryData> salaryData;
}