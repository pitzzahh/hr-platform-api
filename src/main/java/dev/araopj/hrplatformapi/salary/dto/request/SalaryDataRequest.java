package dev.araopj.hrplatformapi.salary.dto.request;

import lombok.Builder;

@Builder
public record SalaryDataRequest(
        byte step,
        double amount,
        String salaryGradeId
) implements SalaryDataBase {
    @Builder
    public record WithoutSalaryGradeId(
            byte step,
            double amount
    ) implements SalaryDataBase {
    }
}