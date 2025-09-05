package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentTypeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentTypeResponse;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing idDocument types.
 * <p>
 * This interface defines methods for retrieving and creating idDocument types.
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
     */
    Optional<IdDocumentTypeResponse> findById(String id);

    /**
     * Creates a new idDocument type.
     *
     * @param request the {@link IdDocumentTypeRequest} containing the details of the idDocument type to create.
     * @return the created {@link IdDocumentTypeResponse}.
     */
    IdDocumentTypeResponse create(IdDocumentTypeRequest request);

}
