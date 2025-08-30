package dev.araopj.hrplatformapi.employee.model;

import dev.araopj.hrplatformapi.utils.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CivilStatus implements EnumUtil.HasCode {
    SINGLE("S", "Single"),
    MARRIED("M", "Married"),
    WIDOWED("W", "Widowed");

    private final String code;
    private final String description;
}