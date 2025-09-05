package dev.araopj.hrplatformapi.employee.dto.request;

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
        @NotNull
        Long employeeNumber,
        @NotNull
        @NotBlank
        String itemNumber,
        @NotNull
        @NotBlank
        String firstName,
        String middleName,
        @NotNull
        @NotBlank
        String lastName,
        String photo,
        @NotNull
        LocalDate dateOfBirth,
        @NotNull
        @NotBlank
        @Email
        String email,
        @NotNull
        @NotBlank
        @Pattern(
                regexp = "^(09\\d{9}|9\\d{9}|\\+639\\d{9})$",
                message = "Phone number must be: 11 digits starting with 09, or 10 digits starting with 9, or 13 characters starting with +639"
        )
        String phoneNumber,
        @NotNull
        Gender gender,
        @NotNull
        @NotBlank
        String taxPayerIdentificationNumber,
        @NotNull
        CivilStatus civilStatus,
        String bankAccountNumber,
        boolean archived,
        String userId,
        Set<IdDocumentRequest> idDocumentRequests,
        Set<EmploymentInformationRequest> employmentInformationRequests
) {
    public record WithoutArchivedAndUserIdAndIdentifierRequestsAndEmploymentInformationRequests(
            @NotNull
            Long employeeNumber,
            @NotNull
            @NotBlank
            String itemNumber,
            @NotNull
            @NotBlank
            String firstName,
            String middleName,
            @NotNull
            @NotBlank
            String lastName,
            String photo,
            @NotNull
            LocalDate dateOfBirth,
            @NotNull
            @NotBlank
            @Email
            String email,
            @NotNull
            @NotBlank
            @Pattern(
                    regexp = "^(09\\d{9}|9\\d{9}|\\+639\\d{9})$",
                    message = "Phone number must be: 11 digits starting with 09, or 10 digits starting with 9, or 13 characters starting with +639"
            )
            String phoneNumber,
            @NotNull
            Gender gender,
            @NotNull
            @NotBlank
            String taxPayerIdentificationNumber,
            @NotNull
            CivilStatus civilStatus,
            String bankAccountNumber) {

    }
}
