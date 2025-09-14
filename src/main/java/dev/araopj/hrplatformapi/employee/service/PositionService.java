package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.PositionRequest;
import dev.araopj.hrplatformapi.employee.dto.response.PositionResponse;
import dev.araopj.hrplatformapi.employee.model.Position;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service interface for managing position-related operations.
 * Provides methods for retrieving, creating, updating, and deleting position records.
 */
public interface PositionService {

    /**
     * Retrieves a paginated list of all positions.
     *
     * @param pageable the pagination and sorting parameters
     * @return a {@link Page} containing {@link Position} objects
     */
    Page<PositionResponse> findAll(Pageable pageable);

    /**
     * Retrieves a position by its unique id.
     *
     * @param id the unique id of the position
     * @return an {@link Optional} containing the {@link Position} if found, or empty if not found
     */
    Optional<PositionResponse> findById(String id);

    /**
     * Creates a new position based on the provided request data.
     *
     * @param positionRequest the request object containing position details
     * @return the created {@link PositionResponse} object
     */
    PositionResponse create(PositionRequest positionRequest);

    /**
     * Updates an existing position with the provided request data.
     *
     * @param id              the unique id of the position to update
     * @param positionRequest the request object containing updated position details
     * @return the updated {@link PositionResponse} object
     */
    PositionResponse update(String id, PositionRequest.WithoutEmploymentInformationId positionRequest) throws InvalidRequestException;

    /**
     * Deletes a position by its unique id.
     *
     * @param id the unique id of the position to delete
     * @return {@code true} if the position was successfully deleted, {@code false} otherwise
     */
    boolean delete(String id);
}