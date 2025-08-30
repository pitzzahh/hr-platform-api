package dev.araopj.hrplatformapi.user.model;

import dev.araopj.hrplatformapi.utils.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStatus implements EnumUtil.HasCode {
    ENABLED("E", "Enabled user"),
    DISABLED("D", "Disabled user");

    private final String code;
    private final String description;
}
