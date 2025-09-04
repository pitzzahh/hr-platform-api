package dev.araopj.hrplatformapi.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStatus {
    ENABLED("E", "Enabled user"),
    DISABLED("D", "Disabled user");

    private final String code;
    private final String description;
}
