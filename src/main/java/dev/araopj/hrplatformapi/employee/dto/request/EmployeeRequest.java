package dev.araopj.hrplatformapi.employee.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.araopj.hrplatformapi.employee.model.CivilStatus;
import dev.araopj.hrplatformapi.employee.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record EmployeeRequest(
        @NotNull(message = "Employee number is required")
        @NotBlank(message = "Employee number cannot be empty")
        @Pattern(
                regexp = "^\\d+$",
                message = "Employee number must contain only digits (e.g., 12345)")
        String employeeNumber,
        @NotNull(message = "Item number is required")
        @NotBlank(message = "Item number cannot be empty")
        String itemNumber,
        @NotNull(message = "First name is required")
        @NotBlank(message = "First name cannot be empty")
        String firstName,
        String middleName,
        @NotNull(message = "Last name is required")
        @NotBlank(message = "Last name cannot be empty")
        String lastName,
        String photo,
        @NotNull(message = "Date of birth is required")
        LocalDate dateOfBirth,
        @NotNull(message = "Email address is required")
        @NotBlank(message = "Email address cannot be empty")
        @Email(message = "Email address must be valid (e.g., user@example.com)")
        String email,
        @NotNull(message = "Phone number is required")
        @NotBlank(message = "Phone number cannot be empty")
        @Pattern(
                regexp = "^(09\\d{9}|9\\d{9}|\\+639\\d{9})$",
                message = "Phone number must be 11 digits starting with 09, 10 digits starting with 9, or 13 characters starting with +639 (e.g., +639123456789)")
        String phoneNumber,
        @NotNull(message = "Gender is required")
        Gender gender,
        @NotNull(message = "Taxpayer identification number is required")
        @NotBlank(message = "Taxpayer identification number cannot be empty")
        String taxPayerIdentificationNumber,
        @NotNull(message = "Civil status is required")
        CivilStatus civilStatus,
        String bankAccountNumber,
        boolean archived,
        String userId,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        Set<IdDocumentRequest> idDocumentRequests,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        Set<EmploymentInformationRequest> employmentInformationRequests
) {
    public record WithoutArchivedAndUserIdAndIdentifierRequestsAndEmploymentInformationRequests(
            @NotNull(message = "Employee number is required")
            @NotBlank(message = "Employee number cannot be empty")
            @Pattern(
                    regexp = "^\\d+$",
                    message = "Employee number must contain only digits (e.g., 12345)"
            )
            String employeeNumber,
            @NotNull(message = "Item number is required")
            @NotBlank(message = "Item number cannot be empty")
            String itemNumber,
            @NotNull(message = "First name is required")
            @NotBlank(message = "First name cannot be empty")
            String firstName,
            String middleName,
            @NotNull(message = "Last name is required")
            @NotBlank(message = "Last name cannot be empty")
            String lastName,
            String photo,
            @NotNull(message = "Date of birth is required")
            LocalDate dateOfBirth,
            @NotNull(message = "Email address is required")
            @NotBlank(message = "Email address cannot be empty")
            @Email(message = "Email address must be valid (e.g., user@example.com)")
            String email,
            @NotNull(message = "Phone number is required")
            @NotBlank(message = "Phone number cannot be empty")
            @Pattern(
                    regexp = "^(09\\d{9}|9\\d{9}|\\+639\\d{9})$",
                    message = "Phone number must be 11 digits starting with 09, 10 digits starting with 9, or 13 characters starting with +639 (e.g., +639123456789)")
            String phoneNumber,
            @NotNull(message = "Gender is required")
            Gender gender,
            @NotNull(message = "Taxpayer identification number is required")
            @NotBlank(message = "Taxpayer identification number cannot be empty")
            String taxPayerIdentificationNumber,
            @NotNull(message = "Civil status is required")
            CivilStatus civilStatus,
            String bankAccountNumber) {
    }
}