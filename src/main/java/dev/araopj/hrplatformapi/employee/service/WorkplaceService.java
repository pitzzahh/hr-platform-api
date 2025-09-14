package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.WorkplaceRequest;
import dev.araopj.hrplatformapi.employee.dto.response.WorkplaceResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service interface for managing workplace-related operations.
 * Provides methods for retrieving, creating, updating, and deleting workplace records.
 */
public interface WorkplaceService {

    /**
     * Retrieves a paginated list of all workplaces.
     *
     * @param pageable the pagination and sorting parameters
     * @return a {@link Page} containing {@link WorkplaceResponse} objects
     */
    Page<WorkplaceResponse> findAll(Pageable pageable);

    /**
     * Retrieves a workplace by its unique identifier.
     *
     * @param id the unique identifier of the workplace
     * @return an {@link Optional} containing the {@link WorkplaceResponse} if found, or empty if not found
     */
    Optional<WorkplaceResponse> findById(String id);

    /**
     * Creates a new workplace based on the provided request data.
     *
     * @param workplaceRequest the request object containing workplace details
     * @return the created {@link WorkplaceResponse} object
     */
    WorkplaceResponse create(WorkplaceRequest workplaceRequest);

    /**
     * Updates an existing workplace with the provided request data.
     *
     * @param id               the unique identifier of the workplace to update
     * @param workplaceRequest the request object containing updated workplace details, excluding employment information ID
     * @return the updated {@link WorkplaceResponse} object
     * @throws BadRequestException if the provided ID is invalid or the workplace cannot be updated
     */
    WorkplaceResponse update(String id, WorkplaceRequest workplaceRequest) throws BadRequestException;

    /**
     * Deletes a workplace by its unique identifier.
     *
     * @param id the unique identifier of the workplace to delete
     * @return {@code true} if the workplace was successfully deleted, {@code false} otherwise
     */
    boolean delete(String id);
}