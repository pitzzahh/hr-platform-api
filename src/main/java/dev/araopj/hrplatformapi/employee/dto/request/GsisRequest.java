package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GsisRequest {
    @NotBlank(message = "Business Partner Number cannot be blank")
    private String businessPartnerNumber;

    @Null
    private LocalDate issuedDate;

    @Null
    private String issuedPlace;

    @NotBlank(message = "Employee ID cannot be blank")
    private String employeeId;
}
