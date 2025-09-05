package dev.araopj.hrplatformapi.salary.service;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service interface for managing salary data operations in the HR platform.
 * <p>
 * This interface defines the contract for performing CRUD operations on salary data,
 * including retrieval, creation, updating, and deletion of salary data records.
 * Implementations of this interface should handle business logic and interact with
 * the data layer to manage {@link SalaryDataResponse} entities.
 *
 * @see SalaryDataResponse
 * @see SalaryDataRequest
 */
public interface ISalaryDataService {

    /**
     * Retrieves a paginated list of all salary data records.
     * <p>
     * This method fetches salary data records from the data layer, supporting pagination
     * through the provided {@link Pageable} object.
     *
     * @param pageable the pagination information (e.g., page number, size)
     * @return a {@link Page} of {@link SalaryDataResponse} objects containing the salary data records
     */
    Page<SalaryDataResponse> findAll(Pageable pageable);

    /**
     * Retrieves a salary data record by its unique ID.
     * <p>
     * If no record is found with the specified ID, an empty {@link Optional} is returned.
     *
     * @param id the unique ID of the salary data record
     * @return an {@link Optional} containing the {@link SalaryDataResponse} if found, or empty if not found
     * @throws NotFoundException if the salary data record does not exist
     */
    Optional<SalaryDataResponse> findById(String id);

    /**
     * Retrieves a salary data record by its ID and associated salary grade ID.
     * <p>
     * This method ensures the record is associated with the specified salary grade.
     * If no matching record is found, an empty {@link Optional} is returned.
     *
     * @param id            the unique ID of the salary data record
     * @param salaryGradeId the ID of the associated salary grade
     * @return an {@link Optional} containing the {@link SalaryDataResponse} if found, or empty if not found
     * @throws NotFoundException if the salary data record or salary grade does not exist
     */
    Optional<SalaryDataResponse> findByIdAndSalaryGradeId(String id, String salaryGradeId);

    /**
     * Creates a new salary data record with the provided request data.
     * <p>
     * The method validates the request data, including checking for the existence
     * of the associated salary grade and ensuring no conflicts with existing records.
     *
     * @param salaryDataRequest the {@link SalaryDataRequest} containing salary data details
     * @return the created {@link SalaryDataResponse} object
     * @throws IllegalArgumentException if the request is invalid (e.g., missing salary grade or duplicate step and amount)
     * @throws NotFoundException        if the associated salary grade does not exist
     */
    SalaryDataResponse create(SalaryDataRequest salaryDataRequest);

    /**
     * Updates an existing salary data record with the provided request data.
     * <p>
     * The method ensures the record exists and validates the updated data, including
     * checking for conflicts with existing records (e.g., duplicate step and amount for a salary grade).
     *
     * @param id                the unique ID of the salary data record to update
     * @param salaryDataRequest the {@link SalaryDataRequest.WithoutSalaryGradeId} containing updated salary data details
     * @return the updated {@link SalaryDataResponse} object
     * @throws BadRequestException if the request is invalid (e.g., missing ID or invalid salary grade)
     * @throws NotFoundException   if the salary data record or salary grade does not exist
     */
    SalaryDataResponse update(String id, SalaryDataRequest.WithoutSalaryGradeId salaryDataRequest)
            throws BadRequestException;

    /**
     * Deletes a salary data record by its ID and associated salary grade ID.
     * <p>
     * The method verifies that the record exists and is associated with the specified
     * salary grade before deletion. If the record is not found, the method returns {@code false}.
     *
     * @param id            the unique ID of the salary data record to delete
     * @param salaryGradeId the ID of the associated salary grade
     * @return {@code true} if the record was successfully deleted, {@code false} otherwise
     * @throws NotFoundException if the salary data record or salary grade does not exist
     */
    boolean delete(String id, String salaryGradeId);
}