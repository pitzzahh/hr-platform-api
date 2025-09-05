package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentResponse;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing IdDocumnents.
 * <p>
 * This interface defines methods for retrieving, creating, updating, and deleting IdDocumnents.
 *
 * @see IdDocumentResponse
 * @see IdDocumentRequest
 */
public interface IdDocumentService {

    /**
     * Retrieves all IdDocumnents.
     *
     * @return a list of {@link IdDocumentResponse} representing all IdDocumnents.
     */
    List<IdDocumentResponse> findAll();

    /**
     * Retrieves an idDocument by its ID.
     *
     * @param id the ID of the idDocument to retrieve.
     * @return an {@link Optional} containing the {@link IdDocumentResponse} if found, or empty if not found.
     */
    Optional<IdDocumentResponse> findById(String id) throws BadRequestException;

    /**
     * Creates a new idDocument.
     *
     * @param request the {@link IdDocumentRequest} containing the details of the idDocument to create.
     * @return the created {@link IdDocumentResponse}.
     */
    IdDocumentResponse create(IdDocumentRequest request);

    /**
     * Updates an existing idDocument.
     *
     * @param id      the ID of the idDocument to update.
     * @param request the {@link IdDocumentRequest} containing the updated details of the idDocument.
     * @return the updated {@link IdDocumentResponse}.
     */
    IdDocumentResponse update(String id, IdDocumentRequest request) throws BadRequestException;

    /**
     * Deletes an idDocument by its ID.
     *
     * @param id the ID of the idDocument to delete.
     * @return true if the idDocument was successfully deleted, false otherwise.
     */
    boolean deleteById(String id);

}
