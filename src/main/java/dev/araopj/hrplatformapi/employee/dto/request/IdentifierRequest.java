package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record IdentifierRequest(

        @NotNull(message = "identifierNumber cannot be null")
        @NotBlank(message = "identifierNumber cannot be blank")
        String identifierNumber,

        @NotNull(message = "identifierTypeRequest cannot be null")
        @NotBlank(message = "identifierTypeRequest cannot be blank")
        IdentifierTypeRequest identifierTypeRequest,

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
