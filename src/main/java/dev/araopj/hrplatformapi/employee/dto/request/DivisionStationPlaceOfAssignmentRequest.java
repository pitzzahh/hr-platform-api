package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record DivisionStationPlaceOfAssignmentRequest(
        String code,
        String name,
        String shortName,
        @NotBlank(message = "Employment Information ID cannot be blank")
        String employmentInformationId
) implements IDivisionStationPlaceOfAssignment {

    @Builder
    public record WithoutEmploymentInformationId(
            String code,
            String name,
            String shortName
    ) implements IDivisionStationPlaceOfAssignment {
    }
}
