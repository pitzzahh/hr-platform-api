package dev.araopj.hrplatformapi.employee.model;

import dev.araopj.hrplatformapi.utils.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmploymentStatus implements EnumUtil.HasCode {

    PERMANENT("PERM", "Permanent"),
    RESIGNED("RES", "Resigned"),
    TEMPORARY("TEMP", "Temporary"),
    TERMINATED("TERM", "Terminated"),
    ON_LEAVE("OL", "On Leave"),
    CONTRACTUAL("CONT", "Contractual"),
    PROBATIONARY("PROB", "Probationary"),
    RETIRED("RET", "Retired"),
    SECONDMENT("SEC", "Secondment"),
    PROJECT_BASED("PB", "Project Based"),
    CASUAL("CAS", "Casual"),
    PART_TIME("PT", "Part-Time"),
    INTERN("I", "Intern");

    private final String code;
    private final String description;
}
