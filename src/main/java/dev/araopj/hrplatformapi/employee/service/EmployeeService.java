package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.EmployeeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmployeeResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service interface for managing employee-related operations.
 * Provides methods for retrieving, creating, updating, and deleting employee records.
 * <p>
 * Best Practice: Implementations should inject other service classes (e.g., EmploymentInformationService)
 * to handle related entity operations, rather than directly injecting repositories.
 * This promotes modularity, encapsulation of business logic, and separation of concerns.
 * Repositories should be used within service implementations to manage data access,
 * while services orchestrate higher-level business logic and interactions.
 * <p>
 * Example Usage:
 * <pre>
 * {@code
 * @Autowired
 * private EmployeeService employeeService;
 *
 * // Retrieve all employees with pagination
 * Page<EmployeeResponse> employees = employeeService.findAll(Pageable.ofSize(10));
 *
 * // Find employee by ID
 * Optional<EmployeeResponse> employee = employeeService.findById("123");
 *
 * // Create new employee
 * EmployeeResponse newEmployee = employeeService.create(new EmployeeResponse(...));
 *
 * // Update existing employee
 * EmployeeResponse updatedEmployee = employeeService.update("123", updatedResponse);
 *
 * // Delete employee
 * boolean deleted = employeeService.delete("123");
 * }
 * </pre>
 */
public interface EmployeeService {

    /**
     * Retrieves a paginated list of employees.
     *
     * @param pageable                     Pagination information.
     * @param includeIdDocuments           Whether to include ID documents in the response.
     * @param includeEmploymentInformation Whether to include employment information in the response.
     * @return A page of employee responses.
     */
    Page<EmployeeResponse> findAll(Pageable pageable, boolean includeIdDocuments, boolean includeEmploymentInformation);

    /**
     * Retrieves an employee by their ID.
     *
     * @param id The employee ID.
     * @return An optional containing the employee response, or empty if not found.
     */
    Optional<EmployeeResponse> findById(String id);

    /**
     * Retrieves an employee by their user ID.
     *
     * @param userId The user ID associated with the employee.
     * @return An optional containing the employee response, or empty if not found.
     */
    Optional<EmployeeResponse> findByUserId(String userId);

    /**
     * Creates a new employee record.
     *
     * @param employeeRequest The employee data to create.
     * @return The created employee response.
     */
    EmployeeResponse create(EmployeeRequest employeeRequest);

    /**
     * Updates an existing employee record.
     *
     * @param id              The employee ID.
     * @param employeeRequest The updated employee data.
     * @return The updated employee response.
     */
    EmployeeResponse update(String id, EmployeeRequest employeeRequest) throws BadRequestException;

    /**
     * Deletes an employee by their ID.
     *
     * @param id The employee ID.
     * @return True if deletion was successful, false otherwise.
     */
    boolean delete(String id);
}