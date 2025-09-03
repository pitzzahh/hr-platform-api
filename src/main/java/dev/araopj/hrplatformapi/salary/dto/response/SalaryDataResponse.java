package dev.araopj.hrplatformapi.salary.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

@Builder
public record SalaryDataResponse(
        String id,
        int step,
        double amount,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        SalaryGradeResponse salaryGrade,
        Instant createdAt,
        Instant updatedAt
) {
}

