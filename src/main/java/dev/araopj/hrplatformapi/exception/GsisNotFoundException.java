package dev.araopj.hrplatformapi.exception;

public class GsisNotFoundException extends IllegalArgumentException {
    public GsisNotFoundException(String id) {
        super("GSIS record with ID " + id + " not found.");
    }
}
