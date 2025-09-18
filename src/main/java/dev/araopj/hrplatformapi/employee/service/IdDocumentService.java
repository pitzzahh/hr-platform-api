package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentResponse;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing IdDocument.
 * <p>
 * This interface defines methods for retrieving, creating, updating, and deleting IdDocument.
 *
 * @see IdDocumentResponse
 * @see IdDocumentRequest
 */
public interface IdDocumentService {

    /**
     * Retrieves all IdDocument.
     *
     * @return a list of {@link IdDocumentResponse} representing all IdDocument.
     */
    List<IdDocumentResponse> findAll();

    /**
     * Retrieves an IdDocument by its ID.
     *
     * @param id the ID of the IdDocument to retrieve.
     * @return an {@link Optional} containing the {@link IdDocumentResponse} if found, or empty if not found.
     * @throws InvalidRequestException when the IdDocument id is not provided as path
     */
    Optional<IdDocumentResponse> findById(String id) throws InvalidRequestException;

    /**
     * Creates a new IdDocument.
     *
     * @param request the {@link IdDocumentRequest} containing the details of the IdDocument to create.
     * @return the created {@link IdDocumentResponse}.
     */
    IdDocumentResponse create(IdDocumentRequest request);

    /**
     * Updates an existing IdDocument.
     *
     * @param id      the ID of the IdDocument to update.
     * @param request the {@link IdDocumentRequest} containing the updated details of the IdDocument.
     * @return the updated {@link IdDocumentResponse}.
     * @throws InvalidRequestException when the update request is invalid
     */
    IdDocumentResponse update(String id, IdDocumentRequest request) throws InvalidRequestException;

    /**
     * Deletes an IdDocument by its ID.
     *
     * @param id the ID of the IdDocument to delete.
     * @return true if the IdDocument was successfully deleted, false otherwise.
     */
    boolean delete(String id);

}
