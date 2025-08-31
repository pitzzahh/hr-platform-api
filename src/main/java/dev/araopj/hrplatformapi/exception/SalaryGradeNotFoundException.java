package dev.araopj.hrplatformapi.exception;

import lombok.Getter;

/**
 * Exception thrown when a salary grade is not found for a given ID.
 */
@Getter
public class SalaryGradeNotFoundException extends IllegalArgumentException {

    private final String salaryGradeId;
    private final String errorCode;

    /**
     * Constructs a new SalaryGradeNotFoundException with the specified salary grade ID.
     *
     * @param salaryGradeId the ID of the salary grade that was not found
     */
    public SalaryGradeNotFoundException(String salaryGradeId) {
        super("Salary grade with id %s not found, no salary grade to relate".formatted(salaryGradeId));
        this.salaryGradeId = salaryGradeId;
        this.errorCode = "SALARY_GRADE_NOT_FOUND";
    }

    /**
     * Constructs a new SalaryGradeNotFoundException with the specified salary grade ID and cause.
     *
     * @param salaryGradeId the ID of the salary grade that was not found
     * @param cause         the cause of the exception
     */
    public SalaryGradeNotFoundException(String salaryGradeId, Throwable cause) {
        super("Salary grade with id %s not found, no salary grade to relate".formatted(salaryGradeId), cause);
        this.salaryGradeId = salaryGradeId;
        this.errorCode = "SALARY_GRADE_NOT_FOUND";
    }

    /**
     * Constructs a new SalaryGradeNotFoundException with the specified message and salary grade ID.
     *
     * @param message       the detail message
     * @param salaryGradeId the ID of the salary grade that was not found
     */
    public SalaryGradeNotFoundException(String message, String salaryGradeId) {
        super(message);
        this.salaryGradeId = salaryGradeId;
        this.errorCode = "SALARY_GRADE_NOT_FOUND";
    }

}