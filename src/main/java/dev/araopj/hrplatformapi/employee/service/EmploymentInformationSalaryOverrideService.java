package dev.araopj.hrplatformapi.employee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.audit.service.AuditService;
import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationSalaryOverrideRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationSalaryOverrideResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformationSalaryOverride;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationSalaryOverrideRepository;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.*;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.EMPLOYMENT_INFORMATION_SALARY_OVERRIDE;
import static dev.araopj.hrplatformapi.utils.DiffUtil.applyDiff;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;
import static dev.araopj.hrplatformapi.utils.Mapper.toDto;

/**
 * Service class for managing EmploymentInformationSalaryOverride entities.
 * <p>
 * This service provides methods to create, read, update, and delete EmploymentInformationSalaryOverride records.
 * It also integrates with the AuditUtil to log actions performed on these records.
 *
 * @see EmploymentInformationSalaryOverride
 * @see EmploymentInformationSalaryOverrideRepository
 * @see EmploymentInformationRepository
 * @see AuditUtil
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentInformationSalaryOverrideService {

    private final EmploymentInformationSalaryOverrideRepository employmentInformationSalaryOverrideRepository;
    private final EmploymentInformationRepository employmentInformationRepository;
    private final AuditUtil auditUtil;
    private final ObjectMapper objectMapper;
    private final Set<String> REDACTED = Set.of("effectiveDate", "employmentInformation");
    private final String ENTITY_NAME = "EmploymentInformationSalaryOverride";

    /**
     * Find all EmploymentInformationSalaryOverride records
     *
     * @return List of EmploymentInformationSalaryOverrideResponse
     */
    public List<EmploymentInformationSalaryOverrideResponse> findAll() {
        var data = employmentInformationSalaryOverrideRepository.findAll()
                .stream()
                .map(entity -> objectMapper.convertValue(entity, EmploymentInformationSalaryOverrideResponse.class))
                .toList();
        auditUtil.audit(
                VIEW,
                "[]",
                Optional.empty(),
                Map.of(
                        "timestamp", Instant.now().toString(),
                        "entity", ENTITY_NAME,
                        "count", data.size()
                ),
                Optional.empty(),
                ENTITY_NAME
        );

        return data;
    }

    /**
     * Find EmploymentInformationSalaryOverride by id
     *
     * @param id EmploymentInformationSalaryOverride id
     * @return Optional of EmploymentInformationSalaryOverrideResponse
     */
    public Optional<EmploymentInformationSalaryOverrideResponse> findById(String id) {
        auditUtil.audit(
                VIEW,
                "[]",
                Optional.of(Map.of("timestamp", Instant.now().toString(),
                        "entity", ENTITY_NAME,
                        "request_id", id)),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );
        return Optional.ofNullable(employmentInformationSalaryOverrideRepository.findById(id)
                .map(e -> objectMapper.convertValue(e, EmploymentInformationSalaryOverrideResponse.class))
                .orElseThrow(() -> new NotFoundException(id, EMPLOYMENT_INFORMATION_SALARY_OVERRIDE)));
    }

    /**
     * Find EmploymentInformationSalaryOverride by id and employmentInformationId
     *
     * @param id                      EmploymentInformationSalaryOverride id
     * @param employmentInformationId EmploymentInformation id
     * @return Optional of EmploymentInformationSalaryOverrideResponse
     */
    public Optional<EmploymentInformationSalaryOverrideResponse> findByIdAndEmploymentInformationId(String id, String employmentInformationId) {
        var data = employmentInformationSalaryOverrideRepository.findByIdAndEmploymentInformation_Id(id, employmentInformationId);

        if (data.isEmpty()) {
            log.warn("{} not found with id {} and employmentInformationId {}", ENTITY_NAME, id, employmentInformationId);
            throw new NotFoundException(id, employmentInformationId, EMPLOYMENT_INFORMATION_SALARY_OVERRIDE, "employmentInformationId");
        }

        auditUtil.audit(
                VIEW,
                id,
                Optional.empty(),
                redact(data.get(), REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );
        return data.map(Mapper::toDto);
    }

    /**
     * Creates a new {@link EmploymentInformationSalaryOverride} entity for the specified employment information.
     * <p>
     * This method validates the **employmentInformationId** from the request body or path variable, ensuring it exists
     * in the {@link EmploymentInformationRepository}. If the ID from the request body is invalid or not found, it falls
     * back to the path variable's ID. If neither ID is valid, an {@link NotFoundException()} is thrown.
     * Upon successful validation, the salary override is saved, and an audit record is created via {@link AuditService}.
     *
     * @param employmentInformationSalaryOverrideRequest The request object containing the salary override details, such as
     *                                                   `salary`, `effectiveDate`, and optionally an
     *                                                   **employmentInformationId**.
     *                                                   Must not be null.
     * @param employmentInformationId                    The **employmentInformationId** from the path variable, used as a
     *                                                   fallback if the request's ID is invalid or missing.
     *                                                   Must not be null.
     * @return An {@link Optional} containing the created {@link EmploymentInformationSalaryOverrideResponse}, or empty if
     * creation fails (though typically, an exception is thrown for invalid cases).
     * @throws NotFoundException() If neither the **employmentInformationId** from the request nor the
     *                             path variable corresponds to a valid {@link EmploymentInformation}.
     * @see EmploymentInformationSalaryOverride
     * @see EmploymentInformationRepository
     * @see AuditService
     */
    public Optional<EmploymentInformationSalaryOverrideResponse> create(
            EmploymentInformationSalaryOverrideRequest employmentInformationSalaryOverrideRequest,
            String employmentInformationId
    ) {
        var employment_information_id_from_request = employmentInformationSalaryOverrideRequest.employmentInformationId();
        var optionalEmploymentInformation = employmentInformationRepository.findById(employment_information_id_from_request);

        if (optionalEmploymentInformation.isEmpty()) {
            log.warn("Checking employment information id from request [{}] not found, falling back to path variable [{}]",
                    employment_information_id_from_request,
                    employmentInformationId
            );
            optionalEmploymentInformation = employmentInformationRepository.findById(employmentInformationId);
            if (optionalEmploymentInformation.isEmpty()) {
                throw new NotFoundException(
                        employment_information_id_from_request,
                        EMPLOYMENT_INFORMATION_SALARY_OVERRIDE
                );
            }
        }

        var data = toDto(employmentInformationSalaryOverrideRepository.saveAndFlush(
                EmploymentInformationSalaryOverride.builder()
                        .salary(employmentInformationSalaryOverrideRequest.salary())
                        .effectiveDate(employmentInformationSalaryOverrideRequest.effectiveDate())
                        .employmentInformation(optionalEmploymentInformation.get())
                        .build()
        ));

        auditUtil.audit(
                CREATE,
                data.id(),
                Optional.empty(),
                redact(data, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );

        return Optional.of(data);
    }

    /**
     * Updates an existing EmploymentInformationSalaryOverride.
     *
     * @param id                                         The unique identifier of the EmploymentInformationSalaryOverride from the path variable.
     * @param employmentInformationId                    The unique identifier of the EmploymentInformation from the path variable.
     * @param employmentInformationSalaryOverrideRequest The request object containing updated data for the EmploymentInformationSalaryOverride. See {@link EmploymentInformationSalaryOverrideRequest}.
     * @param fetchType                                  The strategy to use for fetching the existing record. See {@link FetchType}.
     * @param useParentIdFromPathVariable                If true, uses the employmentInformationId from the path variable for validation; otherwise, uses the ID from the request body.
     * @return An {@link EmploymentInformationSalaryOverrideResponse}.
     * @throws NotFoundException() if no EmploymentInformationSalaryOverride is found with the provided id and employmentInformationId.
     */
    public EmploymentInformationSalaryOverrideResponse update(
            String id,
            String employmentInformationId,
            EmploymentInformationSalaryOverrideRequest employmentInformationSalaryOverrideRequest,
            FetchType fetchType,
            boolean useParentIdFromPathVariable
    ) {
        var employmentInformationSalaryOverrideResponse = switch (fetchType) {
            case BY_PATH_VARIABLE -> findById(id);
            case WITH_PARENT_PATH_VARIABLE ->
                    findByIdAndEmploymentInformationId(id, useParentIdFromPathVariable ? employmentInformationId : employmentInformationSalaryOverrideRequest.employmentInformationId());
        };

        if (employmentInformationSalaryOverrideResponse.isEmpty()) {
            log.warn("EmploymentInformationSalaryOverride with id {} and employment information id {} not found", id, employmentInformationId);
            throw new NotFoundException(
                    id,
                    employmentInformationId,
                    EMPLOYMENT_INFORMATION_SALARY_OVERRIDE,
                    "employmentInformationId"
            );
        }

        var oldData = employmentInformationSalaryOverrideResponse.get(); // Keep the old data for auditing
        var newData = MergeUtil.merge(employmentInformationSalaryOverrideResponse.get(), employmentInformationSalaryOverrideRequest); // Merge old data with new request data
        var changes = DiffUtil.diff(employmentInformationSalaryOverrideResponse.get(), employmentInformationSalaryOverrideRequest); // Compute the diff between old and new data

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(oldData, REDACTED)),
                redact(newData, REDACTED),
                Optional.of(redact(changes, REDACTED)),
                ENTITY_NAME
        );

        return objectMapper.convertValue(
                employmentInformationSalaryOverrideRepository.save(
                        objectMapper.convertValue(applyDiff(
                                        oldData,
                                        changes
                                ), EmploymentInformationSalaryOverride.class
                        )
                ),
                EmploymentInformationSalaryOverrideResponse.class
        );
    }

    /**
     * Deletes an {@link dev.araopj.hrplatformapi.employee.model.EmploymentInformationSalaryOverride} by its ID and associated
     * {@link dev.araopj.hrplatformapi.employee.model.EmploymentInformation} ID.
     * <p>
     * This method first checks if the salary override exists for the given IDs using
     * {@link #findByIdAndEmploymentInformationId(String, String)}. If not found, it logs a warning and returns false.
     *
     * @param id                      the ID of the {@link dev.araopj.hrplatformapi.employee.model.EmploymentInformationSalaryOverride} to delete
     * @param employmentInformationId the ID of the associated {@link dev.araopj.hrplatformapi.employee.model.EmploymentInformation}
     * @return true if the deletion was successful
     * @throws NotFoundException() if the salary override with the specified IDs does not exist
     * @see EmploymentInformationSalaryOverrideRepository
     * @see dev.araopj.hrplatformapi.audit.service.AuditService
     */
    public boolean delete(String id, String employmentInformationId) {
        var data = findByIdAndEmploymentInformationId(id, employmentInformationId);
        employmentInformationSalaryOverrideRepository.deleteById(id);
        auditUtil.audit(
                DELETE,
                id,
                Optional.of(redact(data, REDACTED)),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );
        return true;
    }
}
