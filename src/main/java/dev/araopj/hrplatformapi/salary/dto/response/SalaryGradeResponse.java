package dev.araopj.hrplatformapi.salary.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record SalaryGradeResponse(
        String id,
        String legalBasis,
        byte tranche,
        LocalDate effectiveDate,
        byte salaryGrade,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Set<SalaryData> salaryData
) {
}
