package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.SalaryRequest;
import dev.araopj.hrplatformapi.employee.dto.response.SalaryResponse;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service interface for managing salary-related operations.
 * Provides methods for retrieving, creating, updating, and deleting salary records.
 */
public interface SalaryService {
    /**
     * Retrieves a paginated list of all salaries.
     *
     * @param pageable the pagination and sorting parameters
     * @return a {@link Page} containing {@link SalaryResponse} objects
     */
    Page<SalaryResponse> findAll(Pageable pageable);

    /**
     * Retrieves a salary by its unique id.
     *
     * @param id the unique id of the salary
     * @return an {@link Optional} containing the {@link SalaryResponse} if found, or empty if not found
     */
    Optional<SalaryResponse> findById(String id);

    /**
     * Creates a new salary based on the provided request data.
     *
     * @param salaryRequest the request object containing salary details
     * @return the created {@link SalaryRequest} object
     */
    SalaryResponse create(SalaryRequest salaryRequest);

    /**
     * Updates an existing salary with the provided request data.
     *
     * @param id            the unique id of the salary to update
     * @param salaryRequest the request object containing updated salary details
     * @return the updated {@link SalaryResponse} object
     */
    SalaryResponse update(String id, SalaryRequest salaryRequest) throws InvalidRequestException;

    /**
     * Deletes a salary by its unique id.
     *
     * @param id the unique id of the salary to delete
     * @return {@code true} if the salary was successfully deleted, {@code false} otherwise
     */
    boolean delete(String id);
}
