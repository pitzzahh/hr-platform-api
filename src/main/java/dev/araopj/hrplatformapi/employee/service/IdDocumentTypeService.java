package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentTypeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentTypeResponse;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing idDocument types.
 * <p>
 * This interface defines methods for creating, retrieving, updating, and deleting idDocument types.
 *
 * @see IdDocumentTypeResponse
 * @see IdDocumentTypeRequest
 */
public interface IdDocumentTypeService {

    /**
     * Retrieves all idDocument types.
     *
     * @return a list of {@link IdDocumentTypeResponse} representing all idDocument types.
     */
    List<IdDocumentTypeResponse> findAll();

    /**
     * Retrieves an idDocument type by its ID.
     *
     * @param id the ID of the idDocument type to retrieve.
     * @return an {@link Optional} containing the {@link IdDocumentTypeResponse} if found, or empty if not found.
     * @throws InvalidRequestException if the provided id is invalid.
     */
    Optional<IdDocumentTypeResponse> findById(String id) throws InvalidRequestException;

    /**
     * Creates a new idDocument type.
     *
     * @param request the {@link IdDocumentTypeRequest} containing the details of the idDocument type to create.
     * @return the created {@link IdDocumentTypeResponse}.
     */
    IdDocumentTypeResponse create(IdDocumentTypeRequest request);

    /**
     * Updates an existing idDocument type.
     *
     * @param id                    the ID of the idDocument type to update.
     * @param idDocumentTypeRequest the {@link IdDocumentTypeRequest} containing the updated details.
     * @return the updated {@link IdDocumentTypeResponse}.
     * @throws dev.araopj.hrplatformapi.exception.InvalidRequestException if the provided ID is invalid.
     */
    IdDocumentTypeResponse update(String id, IdDocumentTypeRequest idDocumentTypeRequest) throws InvalidRequestException;

    /**
     * Deletes an idDocument type by its ID.
     *
     * @param id the ID of the idDocument type to delete.
     * @return true if the idDocument type was successfully deleted, false otherwise.
     */
    boolean delete(String id);
}
