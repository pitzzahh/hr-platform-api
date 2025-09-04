package dev.araopj.hrplatformapi.employee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.araopj.hrplatformapi.employee.model.Employee;
import lombok.Builder;

import java.time.Instant;

@Builder
public record IdentifierResponse(
        String id,
        String identifierNumber,
        IdentifierTypeResponse type,
        Instant issuedDate,
        Instant issuedPlace,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Employee employee
) {
}
