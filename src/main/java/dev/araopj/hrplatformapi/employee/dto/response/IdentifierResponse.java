package dev.araopj.hrplatformapi.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.araopj.hrplatformapi.employee.model.Employee;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder
public record IdentifierResponse(
        String id,
        String identifierNumber,
        IdentifierTypeResponse type,
        LocalDate issuedDate,
        String issuedPlace,
        Instant createdAt,
        Instant updatedAt,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Employee employee
) {
}
