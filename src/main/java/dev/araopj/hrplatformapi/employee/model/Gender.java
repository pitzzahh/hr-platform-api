package dev.araopj.hrplatformapi.employee.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender {
    MALE("M", "Male"),
    FEMALE("F", "Female"),
    OTHER("O", "Other");

    private final String code;
    private final String description;
}