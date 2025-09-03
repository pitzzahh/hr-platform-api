package dev.araopj.hrplatformapi.employee.dto.request;

import jakarta.validation.constraints.NotBlank;

public sealed interface IIdentifierType permits IdentifierTypeRequest, IdentifierTypeRequest.WithoutIdentifierId {
    @NotBlank(message = "Code is required")
    String code();

    @NotBlank(message = "Name is required")
    String name();

    String description();
}
