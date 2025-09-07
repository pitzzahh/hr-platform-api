package dev.araopj.hrplatformapi.employee.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmploymentStatus {

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
