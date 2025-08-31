package dev.araopj.hrplatformapi.exception;

public class GsisNotFoundException extends IllegalArgumentException {
    public GsisNotFoundException(String id) {
        super("GSIS record with ID " + id + " not found.");
    }

    public GsisNotFoundException(String id, String employeeId) {
        super("Gsis record with id %s and employeeId %s not found.".formatted(id, employeeId));
    }
}
