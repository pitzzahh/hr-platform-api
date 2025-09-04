package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.IdentifierRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdentifierResponse;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing identifiers.
 * <p>
 * This interface defines methods for retrieving, creating, updating, and deleting identifiers.
 *
 * @see IdentifierResponse
 * @see IdentifierRequest
 */
public interface IIdentifierService {

    /**
     * Retrieves all identifiers.
     *
     * @return a list of {@link IdentifierResponse} representing all identifiers.
     */
    List<IdentifierResponse> findAll();

    /**
     * Retrieves an identifier by its ID.
     *
     * @param id the ID of the identifier to retrieve.
     * @return an {@link Optional} containing the {@link IdentifierResponse} if found, or empty if not found.
     */
    Optional<IdentifierResponse> findById(String id) throws BadRequestException;

    /**
     * Creates a new identifier.
     *
     * @param request the {@link IdentifierRequest} containing the details of the identifier to create.
     * @return the created {@link IdentifierResponse}.
     */
    IdentifierResponse create(IdentifierRequest request);

    /**
     * Updates an existing identifier.
     *
     * @param id      the ID of the identifier to update.
     * @param request the {@link IdentifierRequest} containing the updated details of the identifier.
     * @return the updated {@link IdentifierResponse}.
     */
    IdentifierResponse update(String id, IdentifierRequest request) throws BadRequestException;

    /**
     * Deletes an identifier by its ID.
     *
     * @param id the ID of the identifier to delete.
     * @return true if the identifier was successfully deleted, false otherwise.
     */
    boolean deleteById(String id);

}
