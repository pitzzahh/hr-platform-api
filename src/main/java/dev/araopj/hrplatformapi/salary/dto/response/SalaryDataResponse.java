package dev.araopj.hrplatformapi.salary.dto.response;

import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import lombok.Builder;

import java.time.Instant;

@Builder
public record SalaryDataResponse(
        String id,
        byte step,
        Double amount,
        SalaryGrade salaryGrade,
        Instant createdAt,
        Instant updatedAt
) {
}

