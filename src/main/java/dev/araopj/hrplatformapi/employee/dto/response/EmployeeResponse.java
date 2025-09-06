package dev.araopj.hrplatformapi.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.araopj.hrplatformapi.employee.model.CivilStatus;
import dev.araopj.hrplatformapi.employee.model.Gender;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record EmployeeResponse(
        String id,
        Long employeeNumber,
        String itemNumber,
        String firstName,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String middleName,
        String lastName,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String photo,
        LocalDate dateOfBirth,
        String email,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String phoneNumber,
        Gender gender,
        String taxPayerIdentificationNumber,
        CivilStatus civilStatus,
        String bankAccountNumber,
        boolean archived,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String userId,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Set<IdDocumentResponse> idDocumentResponses,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Set<EmploymentInformationResponse> employmentInformationResponses
) {
}
