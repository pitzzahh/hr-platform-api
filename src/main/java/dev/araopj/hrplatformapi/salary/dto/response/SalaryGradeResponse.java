package dev.araopj.hrplatformapi.salary.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Builder
public record SalaryGradeResponse(
        String id,
        String legalBasis,
        byte tranche,
        LocalDate effectiveDate,
        byte salaryGrade,
        Instant createdAt,
        Instant updatedAt,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<SalaryDataResponse> salaryData
) {
}
