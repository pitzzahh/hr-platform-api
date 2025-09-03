package dev.araopj.hrplatformapi.salary.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import lombok.Builder;

import java.time.Instant;

@Builder
public record SalaryDataResponse(
        String id,
        byte step,
        double amount,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        SalaryGradeResponse salaryGrade,
        Instant createdAt,
        Instant updatedAt
) {
}

