package dev.araopj.hrplatformapi.audit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuditAction {

    CREATE("C", "Create operation"),
    UPDATE("U", "Update operation"),
    DELETE("D", "Delete operation"),
    VIEW("V", "View operation"),
    ERROR("E", "Error operation");

    private final String code;
    private final String description;

}
