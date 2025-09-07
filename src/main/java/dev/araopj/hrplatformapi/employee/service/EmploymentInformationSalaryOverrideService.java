package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationSalaryOverrideRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationSalaryOverrideResponse;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing Employment Information Salary Overrides.
 */
public interface EmploymentInformationSalaryOverrideService {

    /**
     * Retrieves all Employment Information Salary Overrides.
     *
     * @return a list of EmploymentInformationSalaryOverrideResponse
     */
    List<EmploymentInformationSalaryOverrideResponse> findAll();

    /**
     * Retrieves an Employment Information Salary Override by its ID.
     *
     * @param id the ID of the Employment Information Salary Override
     * @return a list of EmploymentInformationSalaryOverrideResponse
     */
    Optional<EmploymentInformationSalaryOverrideResponse> findById(String id);

    /**
     * Creates a new Employment Information Salary Override.
     *
     * @param employmentInformationSalaryOverrideRequest the request object containing the details of the Employment Information Salary Override to be created
     * @return the created EmploymentInformationSalaryOverrideResponse
     */
    EmploymentInformationSalaryOverrideResponse create(EmploymentInformationSalaryOverrideRequest employmentInformationSalaryOverrideRequest);

    /**
     * Updates an existing Employment Information Salary Override.
     *
     * @param id                                         the ID of the Employment Information Salary Override to be updated
     * @param employmentInformationSalaryOverrideRequest the request object containing the updated details of the Employment Information Salary Override
     * @return the updated EmploymentInformationSalaryOverrideResponse
     */
    EmploymentInformationSalaryOverrideResponse update(String id, EmploymentInformationSalaryOverrideRequest.WithoutEmploymentInformationId employmentInformationSalaryOverrideRequest) throws BadRequestException;

    /**
     * Deletes an Employment Information Salary Override by its ID.
     *
     * @param id the ID of the Employment Information Salary Override to be deleted
     * @return true if the deletion was successful, false otherwise
     */
    boolean delete(String id);
}
