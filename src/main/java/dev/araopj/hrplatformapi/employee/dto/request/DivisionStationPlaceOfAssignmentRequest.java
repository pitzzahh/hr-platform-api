package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Builder;

@Builder
public record DivisionStationPlaceOfAssignmentRequest(
        @NotBlank(message = "Code cannot be blank")
        String code,
        @NotBlank(message = "Name cannot be blank")
        String name,
        @Null
        String shortName,
        @NotBlank(message = "Employment Information ID cannot be blank")
        String employmentInformationId
) {
}
