package dev.araopj.hrplatformapi.audit.service;

import dev.araopj.hrplatformapi.audit.dto.request.AuditRequest;
import dev.araopj.hrplatformapi.audit.dto.request.IAuditRequest;
import dev.araopj.hrplatformapi.audit.dto.response.AuditResponse;
import dev.araopj.hrplatformapi.audit.model.Audit;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service interface for managing audit records in the HR platform.
 * <p>
 * This interface defines the contract for retrieving and creating audit records.
 * Implementations handle business logic for auditing actions on entities, such as
 * tracking changes and storing audit metadata.
 *
 * @see Audit
 * @see AuditRequest
 * @see AuditResponse
 */
public interface IAuditService {

    /**
     * Retrieves a paginated list of all audit records.
     * <p>
     * This method fetches audit records from the data layer, supporting pagination
     * through the provided {@link Pageable} object.
     *
     * @param pageable the pagination information (e.g., page number, size)
     * @return a {@link Page} of {@link AuditResponse} objects containing the audit records
     */
    Page<AuditResponse> findAll(Pageable pageable);

    /**
     * Retrieves an audit record by its unique ID.
     * <p>
     * If no record is found with the specified ID, an empty {@link Optional} is returned.
     *
     * @param id the unique ID of the audit record
     * @return an {@link Optional} containing the {@link AuditResponse} if found, or empty if not found
     */
    Optional<AuditResponse> findById(@NotNull String id);

    /**
     * Creates a new audit record based on the provided request data.
     * <p>
     * The method validates the input and persists the audit record, logging a warning
     * if the audit record indicates no changes (e.g., no old or new data).
     *
     * @param request the {@link IAuditRequest} containing audit details
     * @return the created {@link AuditResponse} object
     */
    AuditResponse create(@NotNull IAuditRequest request);
}