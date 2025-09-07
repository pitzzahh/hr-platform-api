package dev.araopj.hrplatformapi.employee.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CivilStatus {
    SINGLE("S", "Single"),
    MARRIED("M", "Married"),
    WIDOWED("W", "Widowed");

    private final String code;
    private final String description;
}