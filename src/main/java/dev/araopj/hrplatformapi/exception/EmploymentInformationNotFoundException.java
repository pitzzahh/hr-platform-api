package dev.araopj.hrplatformapi.exception;

import lombok.Getter;

/**
 * Exception thrown when employment information is not found for a given ID.
 */
@Getter
public class EmploymentInformationNotFoundException extends IllegalArgumentException {

    private final String employmentInformationId;
    private final String errorCode;

    /**
     * Constructs a new EmploymentInformationNotFoundException with the specified employment information ID.
     *
     * @param employmentInformationId the ID of the employment information that was not found
     */
    public EmploymentInformationNotFoundException(String employmentInformationId) {
        super("Employment information with id %s not found, no employment information to relate".formatted(employmentInformationId));
        this.employmentInformationId = employmentInformationId;
        this.errorCode = "EMPLOYMENT_INFORMATION_NOT_FOUND";
    }

    /**
     * Constructs a new EmploymentInformationNotFoundException with the specified employment information ID and cause.
     *
     * @param employmentInformationId the ID of the employment information that was not found
     * @param cause                   the cause of the exception
     */
    public EmploymentInformationNotFoundException(String employmentInformationId, Throwable cause) {
        super("Employment information with id %s not found, no employment information to relate".formatted(employmentInformationId), cause);
        this.employmentInformationId = employmentInformationId;
        this.errorCode = "EMPLOYMENT_INFORMATION_NOT_FOUND";
    }

    /**
     * Constructs a new EmploymentInformationNotFoundException with the specified message and employment information ID.
     *
     * @param message                 the detail message
     * @param employmentInformationId the ID of the employment information that was not found
     */
    public EmploymentInformationNotFoundException(String message, String employmentInformationId) {
        super(message);
        this.employmentInformationId = employmentInformationId;
        this.errorCode = "EMPLOYMENT_INFORMATION_NOT_FOUND";
    }

}
