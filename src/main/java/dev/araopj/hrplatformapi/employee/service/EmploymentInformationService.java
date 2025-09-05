package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service interface for managing employment information-related operations.
 * Provides methods for retrieving, creating, updating, and deleting employment information records.
 */
public interface EmploymentInformationService {

    /**
     * Retrieves a paginated list of all employment information records.
     *
     * @param pageable the pagination and sorting parameters
     * @return a {@link Page} containing {@link EmploymentInformationResponse} objects
     */
    Page<EmploymentInformationResponse> findAll(Pageable pageable);

    /**
     * Retrieves a paginated list of employment information records by employee ID.
     *
     * @param employeeId the unique id of the employee
     * @param pageable   the pagination and sorting parameters
     * @return a {@link Page} containing {@link EmploymentInformationResponse} objects associated with the specified employee ID
     */
    Page<EmploymentInformationResponse> findByEmployeeId(String employeeId, Pageable pageable);

    /**
     * Retrieves an employment information record by its unique id.
     *
     * @param id the unique id of the employment information
     * @return an {@link Optional} containing the {@link EmploymentInformationResponse} if found, or empty if not found
     */
    Optional<EmploymentInformationResponse> findById(String id);

    /**
     * Creates a new employment information record based on the provided request data.
     *
     * @param id                           the unique id of the employee to associate with the employment information
     * @param employmentInformationRequest the request object containing employment information details
     * @return the created {@link EmploymentInformationResponse} object
     */
    EmploymentInformationResponse create(String id, EmploymentInformationRequest employmentInformationRequest);

    /**
     * Updates an existing employment information record with the provided request data.
     *
     * @param id                           the unique id of the employment information to update
     * @param employmentInformationRequest the request object containing updated employment information details
     * @return the updated {@link EmploymentInformationResponse} object
     */
    EmploymentInformationResponse update(String id, EmploymentInformationRequest employmentInformationRequest);

    /**
     * Deletes an employment information record by its unique id.
     *
     * @param id the unique id of the employment information to delete
     * @return {@code true} if the employment information was successfully deleted, {@code false} otherwise
     */
    boolean delete(String id);

}
