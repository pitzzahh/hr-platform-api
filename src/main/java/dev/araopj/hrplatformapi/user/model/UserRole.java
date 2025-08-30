package dev.araopj.hrplatformapi.user.model;

import dev.araopj.hrplatformapi.utils.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole implements EnumUtil.HasCode {

    IT_ADMIN("IT_AD", "IT Administrator"),
    HR_ADMIN("HR_AD", "HR Administrator"),
    COORDINATOR("CO", "Coordinator"),
    EMPLOYEE("E", "Employee");

    private final String code;
    private final String description;
}
