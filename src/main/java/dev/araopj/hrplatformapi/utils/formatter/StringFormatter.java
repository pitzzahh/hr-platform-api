package dev.araopj.hrplatformapi.utils.formatter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringFormatter {

    /**
     * Formats an employee's full name by concatenating first, middle, and last names.
     * It trims leading/trailing spaces and replaces multiple spaces with a single space.
     *
     * @param firstName  The first name of the employee.
     * @param middleName The middle name of the employee (can be null or empty).
     * @param lastName   The last name of the employee.
     * @return A formatted full name string.
     */
    public String formatEmployeeName(String firstName, String middleName, String lastName) {
        return String.join(" ",
                        new String[]{firstName, middleName, lastName})
                .trim()
                .replaceAll("\\s+", " ");
    }
}
