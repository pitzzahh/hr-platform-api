package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.IdentifierTypeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdentifierTypeResponse;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing identifier types.
 * <p>
 * This interface defines methods for retrieving and creating identifier types.
 *
 * @see IdentifierTypeResponse
 * @see IdentifierTypeRequest
 */
public interface IdentifierTypeService {

    /**
     * Retrieves all identifier types.
     *
     * @return a list of {@link IdentifierTypeResponse} representing all identifier types.
     */
    List<IdentifierTypeResponse> findAll();

    /**
     * Retrieves an identifier type by its ID.
     *
     * @param id the ID of the identifier type to retrieve.
     * @return an {@link Optional} containing the {@link IdentifierTypeResponse} if found, or empty if not found.
     */
    Optional<IdentifierTypeResponse> findById(String id);

    /**
     * Creates a new identifier type.
     *
     * @param request the {@link IdentifierTypeRequest} containing the details of the identifier type to create.
     * @return the created {@link IdentifierTypeResponse}.
     */
    IdentifierTypeResponse create(IdentifierTypeRequest request);

}
