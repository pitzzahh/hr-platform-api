package dev.araopj.hrplatformapi.salary.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SalaryDataResponse(String id, byte step, Double amount, String salaryGradeId, LocalDateTime createdAt,
                                 LocalDateTime updatedAt) {
}
