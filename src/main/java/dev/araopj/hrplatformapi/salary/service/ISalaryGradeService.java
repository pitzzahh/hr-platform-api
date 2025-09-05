package dev.araopj.hrplatformapi.salary.service;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing salary grade operations in the HR platform.
 * <p>
 * This interface defines the contract for performing CRUD operations on salary grades,
 * including retrieval, creation, batch creation, updating, and Deletion of salary grade records.
 * Implementations of this interface handle business logic and interact with the data layer
 * to manage {@link SalaryGradeResponse} entities, optionally including associated salary data.
 *
 * @see SalaryGradeResponse
 * @see SalaryGradeRequest
 * @see NotFoundException
 * @see BadRequestException
 */
public interface ISalaryGradeService {

    /**
     * Retrieves a page of salary grade records, optionally including associated salary data.
     * <p>
     * This method fetches all salary grades from the data layer. If {@code includeSalaryData}
     * is true, associated salary data is included in the response.
     *
     * @param pageable          the pagination information (e.g., page number, size)
     * @param includeSalaryData whether to include associated salary data in the response
     * @return a {@link Page} of {@link SalaryGradeResponse} objects containing the salary grades
     */
    Page<SalaryGradeResponse> findAll(Pageable pageable, boolean includeSalaryData);

    /**
     * Retrieves a salary grade record by its unique ID, optionally including associated salary data.
     * <p>
     * If no record is found with the specified ID, an empty {@link Optional} is returned.
     * The {@code includeSalaryData} flag determines whether associated salary data is included.
     *
     * @param id                the unique ID of the salary grade record
     * @param includeSalaryData whether to include associated salary data in the response
     * @return an {@link Optional} containing the {@link SalaryGradeResponse} if found, or empty if not found
     * @throws BadRequestException if the provided ID is null or empty
     */
    Optional<SalaryGradeResponse> findById(String id, boolean includeSalaryData) throws BadRequestException;

    /**
     * Creates a new salary grade record based on the provided request data.
     * <p>
     * The method validates the input to ensure no duplicate salary grades exist (based on salary grade
     * and effective date). If {@code includeSalaryData} is true, associated salary data must be provided
     * in the request.
     *
     * @param salaryGradeRequest the {@link SalaryGradeRequest} containing salary grade details
     * @param includeSalaryData  whether to include associated salary data in creation
     * @return the created {@link SalaryGradeResponse} object
     * @throws BadRequestException if the request is invalid (e.g., duplicate salary grade or missing salary data)
     */
    SalaryGradeResponse create(SalaryGradeRequest salaryGradeRequest, boolean includeSalaryData)
            throws BadRequestException;

    /**
     * Creates a batch of new salary grade records based on the provided list of request data.
     * <p>
     * The method validates each request to ensure no duplicate salary grades exist (based on salary grade
     * and effective date). If {@code includeSalaryData} is true, associated salary data must be provided
     * for each request. All records are saved in a single batch operation.
     *
     * @param salaryGradeRequests the {@link List} of {@link SalaryGradeRequest} objects containing salary grade details
     * @param includeSalaryData   whether to include associated salary data in creation
     * @return a {@link List} of created {@link SalaryGradeResponse} objects
     * @throws BadRequestException if any request is invalid (e.g., duplicate salary grade or missing salary data)
     */
    List<SalaryGradeResponse> createBatch(List<SalaryGradeRequest> salaryGradeRequests, boolean includeSalaryData)
            throws BadRequestException;

    /**
     * Updates an existing salary grade record with the provided request data.
     * <p>
     * The method ensures the record exists and validates the updated data. The updated salary grade
     * is persisted, and an audit record is created to log the changes.
     *
     * @param id                 the unique ID of the salary grade record to update
     * @param salaryGradeRequest the {@link SalaryGradeRequest} containing updated salary grade details
     * @return the updated {@link SalaryGradeResponse} object
     * @throws BadRequestException if the request is invalid (e.g., missing or empty ID)
     * @throws NotFoundException   if the salary grade record does not exist
     */
    SalaryGradeResponse update(String id, SalaryGradeRequest salaryGradeRequest)
            throws BadRequestException;

    /**
     * Deletes a salary grade record by its unique ID.
     * <p>
     * The method verifies that the record exists before deletion. If the record is found, it is deleted,
     * and an audit record is created to log the action.
     *
     * @param id the unique ID of the salary grade record to delete
     * @return {@code true} if the record was successfully deleted
     * @throws NotFoundException if the salary grade record does not exist
     */
    boolean delete(String id);
}