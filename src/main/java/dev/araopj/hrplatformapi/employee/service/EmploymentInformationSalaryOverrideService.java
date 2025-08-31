package dev.araopj.hrplatformapi.employee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.audit.dto.AuditDto;
import dev.araopj.hrplatformapi.audit.model.AuditAction;
import dev.araopj.hrplatformapi.audit.service.AuditService;
import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationSalaryOverrideRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationSalaryOverrideResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformationSalaryOverride;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationSalaryOverrideRepository;
import dev.araopj.hrplatformapi.exception.EmploymentInformationNotFoundException;
import dev.araopj.hrplatformapi.utils.DiffUtil;
import dev.araopj.hrplatformapi.utils.Mapper;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for managing EmploymentInformationSalaryOverride entities.
 * <p>
 * This service provides methods to create, read, update, and delete EmploymentInformationSalaryOverride records.
 * It also integrates with the AuditService to log actions performed on these records.
 *
 * @see EmploymentInformationSalaryOverride
 * @see EmploymentInformationSalaryOverrideRepository
 * @see EmploymentInformationRepository
 * @see AuditService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentInformationSalaryOverrideService {

    private final EmploymentInformationSalaryOverrideRepository employmentInformationSalaryOverrideRepository;
    private final EmploymentInformationRepository employmentInformationRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    /**
     * Find all EmploymentInformationSalaryOverride records
     *
     * @return List of EmploymentInformationSalaryOverrideResponse
     */
    public List<EmploymentInformationSalaryOverrideResponse> findAll() {
        var data = employmentInformationSalaryOverrideRepository.findAll()
                .stream()
                .map(Mapper::toDto)
                .toList();
        auditService.create(
                AuditDto.builder()
                        .action(AuditAction.VIEW)
                        .newData(objectMapper.valueToTree(Map.of(
                                "timestamp", Instant.now().toString(),
                                "entity", "EmploymentInformationSalaryOverride",
                                "count", data.size()
                        )))
                        .performedBy("system")
                        .entityType("EmploymentInformationSalaryOverride")
                        .entityId("N/A")
                        .build()
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
        var data = employmentInformationSalaryOverrideRepository.findById(id);
        auditService.create(
                AuditDto.builder()
                        .action(AuditAction.VIEW)
                        .newData(objectMapper.valueToTree(Map.of(
                                "timestamp", Instant.now().toString(),
                                "entity", "EmploymentInformationSalaryOverride",
                                "id", id,
                                "found", data.isPresent()
                        )))
                        .performedBy("system")
                        .entityType("EmploymentInformationSalaryOverride")
                        .entityId(id)
                        .build()
        );
        return data.map(Mapper::toDto);
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
        auditService.create(
                AuditDto.builder()
                        .action(AuditAction.VIEW)
                        .newData(objectMapper.valueToTree(Map.of(
                                "timestamp", Instant.now().toString(),
                                "entity", "EmploymentInformationSalaryOverride",
                                "id", id,
                                "employmentInformationId", employmentInformationId,
                                "found", data.isPresent()
                        )))
                        .performedBy("system")
                        .entityType("EmploymentInformationSalaryOverride")
                        .entityId(id)
                        .build()
        );
        return data.map(Mapper::toDto);
    }

    /**
     * Creates a new {@link EmploymentInformationSalaryOverride} entity for the specified employment information.
     * <p>
     * This method validates the **employmentInformationId** from the request body or path variable, ensuring it exists
     * in the {@link EmploymentInformationRepository}. If the ID from the request body is invalid or not found, it falls
     * back to the path variable's ID. If neither ID is valid, an {@link EmploymentInformationNotFoundException} is thrown.
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
     * @throws EmploymentInformationNotFoundException If neither the **employmentInformationId** from the request nor the
     *                                                path variable corresponds to a valid {@link EmploymentInformation}.
     * @see EmploymentInformationSalaryOverride
     * @see EmploymentInformationRepository
     * @see AuditService
     */
    public Optional<EmploymentInformationSalaryOverrideResponse> create(
            EmploymentInformationSalaryOverrideRequest employmentInformationSalaryOverrideRequest,
            String employmentInformationId
    ) {
        var employment_information_id_from_request = employmentInformationSalaryOverrideRequest.getEmploymentInformationId();
        var optionalEmploymentInformation = employmentInformationRepository.findById(employment_information_id_from_request);

        if (optionalEmploymentInformation.isEmpty()) {
            log.warn("Checking employment information id from request [{}] not found, falling back to path variable [{}]",
                    employment_information_id_from_request,
                    employmentInformationId
            );
            optionalEmploymentInformation = employmentInformationRepository.findById(employmentInformationId);
            if (optionalEmploymentInformation.isEmpty()) {
                throw new EmploymentInformationNotFoundException(
                        "Employment information id from request: %s and path variable: %s not found".formatted(employment_information_id_from_request, employmentInformationId)
                );
            }
        }

        var data = Mapper.toDto(employmentInformationSalaryOverrideRepository.saveAndFlush(
                EmploymentInformationSalaryOverride.builder()
                        .salary(employmentInformationSalaryOverrideRequest.getSalary())
                        .effectiveDate(employmentInformationSalaryOverrideRequest.getEffectiveDate())
                        .employmentInformation(optionalEmploymentInformation.get())
                        .build()
        ));

        auditService.create(
                AuditDto.builder()
                        .action(AuditAction.CREATE)
                        .newData(objectMapper.valueToTree(data))
                        .performedBy("system")
                        .entityType("EmploymentInformationSalaryOverride")
                        .entityId(data.id())
                        .build()
        );

        return Optional.of(data);
    }

    /**
     * Updates an existing EmploymentInformationSalaryOverride.
     *
     * @param id                                         The unique identifier of the EmploymentInformationSalaryOverride from the path variable.
     * @param employmentInformationId                    The unique identifier of the EmploymentInformation from the path variable.
     * @param employmentInformationSalaryOverrideRequest The request object containing updated data for the EmploymentInformationSalaryOverride. See {@link EmploymentInformationSalaryOverrideRequest}.
     * @param useEmploymentInformationIdFromPath         Indicates whether to use the employmentInformationId from the path variable or the request body.
     * @param checkWithEmploymentInformationIdFromPath   When true and useEmploymentInformationIdFromPath is true, validates the employmentInformationId from the path variable.
     * @return An {@link EmploymentInformationSalaryOverrideResponse}.
     * @throws EmploymentInformationNotFoundException if no EmploymentInformationSalaryOverride is found with the provided id and employmentInformationId.
     */
    public EmploymentInformationSalaryOverrideResponse update(
            String id,
            String employmentInformationId,
            EmploymentInformationSalaryOverrideRequest employmentInformationSalaryOverrideRequest,
            boolean useEmploymentInformationIdFromPath,
            boolean checkWithEmploymentInformationIdFromPath
    ) {
        var optionalEmploymentInformationSalaryOverrideResponse = useEmploymentInformationIdFromPath ?
                findByIdAndEmploymentInformationId(
                        id,
                        checkWithEmploymentInformationIdFromPath ? // TODO: FIx this because it's wrong fr
                                employmentInformationId : employmentInformationSalaryOverrideRequest.getEmploymentInformationId()
                ) : findById(id);

        if (optionalEmploymentInformationSalaryOverrideResponse.isEmpty()) {
            log.warn("Employment information salary override with id {} and employment information id {} not found", id, employmentInformationId);
            throw new EmploymentInformationNotFoundException("Employment information salary override with id %s and employment information id %s not found".formatted(id, employmentInformationId));
        }

        var oldData = optionalEmploymentInformationSalaryOverrideResponse.get(); // Keep the old data for auditing
        var newData = MergeUtil.merge(optionalEmploymentInformationSalaryOverrideResponse.get(), employmentInformationSalaryOverrideRequest); // Merge old data with new request data
        var changes = DiffUtil.diff(optionalEmploymentInformationSalaryOverrideResponse.get(), employmentInformationSalaryOverrideRequest); // Compute the diff between old and new data

        auditService.create(
                AuditDto.builder()
                        .action(AuditAction.UPDATE)
                        .oldData(objectMapper.valueToTree(oldData))
                        .newData(objectMapper.valueToTree(newData))
                        .changes(objectMapper.valueToTree(changes))
                        .performedBy("system")
                        .entityType("EmploymentInformationSalaryOverride")
                        .entityId(id)
                        .build()
        );

        return Mapper.toDto(
                employmentInformationSalaryOverrideRepository.save(
                        Mapper.toEntity(
                                DiffUtil.applyDiff(
                                        optionalEmploymentInformationSalaryOverrideResponse.get(),
                                        changes
                                )
                        )
                )
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
     * @throws EmploymentInformationNotFoundException if the salary override with the specified IDs does not exist
     * @see EmploymentInformationSalaryOverrideRepository
     * @see dev.araopj.hrplatformapi.audit.service.AuditService
     */
    public boolean delete(String id, String employmentInformationId) {
        var data = findByIdAndEmploymentInformationId(id, employmentInformationId);
        if (data.isEmpty()) {
            log.warn("EmploymentInformationSalaryOverride with id {} and employmentInformationId {} not found", id, employmentInformationId);
            throw new EmploymentInformationNotFoundException("EmploymentInformationSalaryOverride with id %s and employmentInformationId %s not found".formatted(id, employmentInformationId));
        }
        if (!employmentInformationSalaryOverrideRepository.existsById(id)) {
            log.warn("EmploymentInformationSalaryOverride with id {} not found for deletion", id);
            throw new EmploymentInformationNotFoundException("EmploymentInformationSalaryOverride with id %s not found for deletion".formatted(id));
        }
        employmentInformationSalaryOverrideRepository.deleteById(id);
        auditService.create(
                AuditDto.builder()
                        .action(AuditAction.DELETE)
                        .newData(objectMapper.valueToTree(Map.of(
                                "timestamp", Instant.now().toString(),
                                "entity", "EmploymentInformationSalaryOverride",
                                "deletedId", id
                        )))
                        .performedBy("system")
                        .entityType("EmploymentInformationSalaryOverride")
                        .entityId(id)
                        .build()
        );
        return true;
    }
}
