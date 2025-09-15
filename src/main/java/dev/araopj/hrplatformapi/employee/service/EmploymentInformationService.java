package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationResponse;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import dev.araopj.hrplatformapi.exception.NotFoundException;
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
     * @throws InvalidRequestException if the provided employee ID is invalid
     */
    Page<EmploymentInformationResponse> findByEmployeeId(String employeeId, Pageable pageable) throws InvalidRequestException, NotFoundException;

    /**
     * Retrieves an employment information record by its unique id.
     *
     * @param id the unique id of the employment information
     * @return an {@link Optional} containing the {@link EmploymentInformationResponse} if found, or empty if not found
     * @throws InvalidRequestException if the provided id is invalid
     * @throws NotFoundException       if the employment information with the specified id does not exist
     */
    Optional<EmploymentInformationResponse> findById(String id) throws InvalidRequestException, NotFoundException;

    /**
     * Creates a new employment information record based on the provided request data.
     *
     * @param id                           the unique id of the employee to associate with the employment information
     * @param employmentInformationRequest the request object containing employment information details
     * @return the created {@link EmploymentInformationResponse} object
     * @throws InvalidRequestException if the provided id is invalid
     * @throws NotFoundException       if the employee with the specified criteria does not exist
     */
    EmploymentInformationResponse create(String id, EmploymentInformationRequest employmentInformationRequest) throws InvalidRequestException, NotFoundException;

    /**
     * Updates an existing employment information record with the provided request data.
     *
     * @param id                           the unique id of the employment information to update
     * @param employmentInformationRequest the request object containing updated employment information details
     * @return the updated {@link EmploymentInformationResponse} object
     * @throws InvalidRequestException if the provided id is invalid or the employment information cannot be updated
     * @throws NotFoundException       if the employment information with the specified id does not exist
     */
    EmploymentInformationResponse update(String id, EmploymentInformationRequest employmentInformationRequest) throws InvalidRequestException, NotFoundException;

    /**
     * Deletes an employment information record by its unique id.
     *
     * @param id the unique id of the employment information to delete
     * @return {@code true} if the employment information was successfully deleted, {@code false} otherwise
     */
    boolean delete(String id);

}
