package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;

public sealed interface IDivisionStationPlaceOfAssignment permits DivisionStationPlaceOfAssignmentRequest, DivisionStationPlaceOfAssignmentRequest.WithoutEmploymentInformationId {
    @NotBlank(message = "Code cannot be blank")
    String code();

    @NotBlank(message = "Name cannot be blank")
    String name();

    String shortName();
}
