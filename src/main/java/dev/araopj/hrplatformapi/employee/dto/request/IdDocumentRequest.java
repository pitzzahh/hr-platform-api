package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record IdDocumentRequest(

        @NotNull(message = "identifierNumber cannot be null")
        @NotBlank(message = "identifierNumber cannot be blank")
        String identifierNumber,

        @NotNull(message = "idDocumentTypeRequest cannot be null")
        @NotBlank(message = "idDocumentTypeRequest cannot be blank")
        IdDocumentTypeRequest idDocumentTypeRequest,

        LocalDate issuedDate,
        String issuedPlace,

        @NotNull(message = "employeeId cannot be null")
        @NotBlank(message = "employeeId cannot be blank")
        String employeeId

) {
    @Builder
    public record WithoutEmployeeId(
            @NotNull(message = "identifierNumber cannot be null")
            @NotBlank(message = "identifierNumber cannot be blank")
            String identifierNumber,

            @NotNull(message = "identifierTypeId cannot be null")
            @NotBlank(message = "identifierTypeId cannot be blank")
            String identifierTypeId,

            LocalDate issuedDate,
            String issuedPlace
    ) {
    }
}
