package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
public class DivisionStationPlaceOfAssignmentRequest {

    @NotBlank(message = "Code cannot be blank")
    private String code;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Null
    private String shortName;

    @NotBlank(message = "Employment Information ID cannot be blank")
    private String employmentInformationId;
}
