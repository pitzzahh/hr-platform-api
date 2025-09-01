package dev.araopj.hrplatformapi.employee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.audit.model.AuditAction;
import dev.araopj.hrplatformapi.employee.dto.request.DivisionStationPlaceOfAssignmentRequest;
import dev.araopj.hrplatformapi.employee.dto.response.DivisionStationPlaceOfAssignmentResponse;
import dev.araopj.hrplatformapi.employee.model.DivisionStationPlaceOfAssignment;
import dev.araopj.hrplatformapi.employee.repository.DivisionStationPlaceOfAssignmentRepository;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.DiffUtil;
import dev.araopj.hrplatformapi.utils.FetchType;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.CREATE;
import static dev.araopj.hrplatformapi.audit.model.AuditAction.UPDATE;
import static dev.araopj.hrplatformapi.utils.DiffUtil.diff;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;
import static dev.araopj.hrplatformapi.utils.MergeUtil.merge;

/**
 * Service class for managing DivisionStationPlaceOfAssignment entities.
 * Provides methods to create, retrieve, update, and audit DivisionStationPlaceOfAssignment records.
 * Utilizes repositories for data access and an audit utility for logging actions.
 * Handles NotFoundExceptions for missing records.
 * Maps between entity and DTO using ObjectMapper.
 * Supports fetching records by ID and employment information ID.
 * Example usage:
 *
 * <pre>
 * {@code
 * DivisionStationPlaceOfAssignmentService service = new DivisionStationPlaceOfAssignmentService(...);
 * List<DivisionStationPlaceOfAssignmentResponse> allRecords = service.findAll();
 * DivisionStationPlaceOfAssignmentResponse record = service.findById("some-id").orElseThrow();}
 * </pre>
 *
 * @author Peter John Arao
 * @see DivisionStationPlaceOfAssignment
 * @see DivisionStationPlaceOfAssignmentRequest
 * @see DivisionStationPlaceOfAssignmentResponse
 * @see DivisionStationPlaceOfAssignmentRepository
 * @see EmploymentInformationRepository
 * @see AuditUtil
 * @see NotFoundException
 * @see ObjectMapper
 * @see FetchType
 * @see DiffUtil
 * @see MergeUtil
 * @see dev.araopj.hrplatformapi.utils.JsonRedactor
 * @since 0.0.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DivisionStationPlaceOfAssignmentService {

    private final DivisionStationPlaceOfAssignmentRepository divisionStationPlaceOfAssignmentRepository;
    private final EmploymentInformationRepository employmentInformationRepository;
    private final ObjectMapper objectMapper;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "employmentInformation");
    private final String ENTITY_NAME = "DivisionStationPlaceOfAssignmentResponse";

    /**
     * Retrieve all {@link DivisionStationPlaceOfAssignment} records and log the action in the audit service.
     *
     * @return List of {@link DivisionStationPlaceOfAssignmentResponse} containing all records.
     */
    public List<DivisionStationPlaceOfAssignmentResponse> findAll() {
        var data = divisionStationPlaceOfAssignmentRepository.findAll()
                .stream()
                .map(entity -> objectMapper.convertValue(entity, DivisionStationPlaceOfAssignmentResponse.class))
                .toList();
        auditUtil.audit(
                AuditAction.VIEW,
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
     * Retrieve a {@link DivisionStationPlaceOfAssignment} by its ID and log the action in the audit service.
     *
     * @param id the ID of the {@link DivisionStationPlaceOfAssignment} to retrieve
     * @return An Optional containing the {@link DivisionStationPlaceOfAssignmentResponse} if found, otherwise empty.
     * @throws NotFoundException if no {@link DivisionStationPlaceOfAssignment} is found with the given ID
     */
    public Optional<DivisionStationPlaceOfAssignmentResponse> findById(String id) throws NotFoundException {
        auditUtil.audit(
                AuditAction.VIEW,
                "[]",
                Optional.empty(),
                Map.of("timestamp", Instant.now().toString(),
                        "entity", ENTITY_NAME,
                        "request_id", id),
                Optional.empty(),
                ENTITY_NAME
        );
        return Optional.ofNullable(divisionStationPlaceOfAssignmentRepository.findById(id)
                .map(e -> objectMapper.convertValue(e, DivisionStationPlaceOfAssignmentResponse.class))
                .orElseThrow(() -> new NotFoundException(id, NotFoundException.EntityType.DIVISION_STATION_PLACE_OF_ASSIGNMENT)));

    }

    /**
     * Retrieve a DivisionStationPlaceOfAssignment record by its ID and associated employee ID, logging the action in the audit service.
     *
     * @param id                      The ID of the DivisionStationPlaceOfAssignment record.
     * @param employmentInformationId The ID of the associated employee.
     * @return DivisionStationPlaceOfAssignmentResponse containing the DivisionStationPlaceOfAssignment record.
     * @throws NotFoundException() if no DivisionStationPlaceOfAssignment record is found with the given ID and employee ID.
     */
    public DivisionStationPlaceOfAssignmentResponse findByIdAndEmploymentInformationId(String id, String employmentInformationId) {
        var data = divisionStationPlaceOfAssignmentRepository.findByIdAndEmploymentInformation_Id(id, employmentInformationId);

        if (data.isEmpty()) {
            log.warn("{} not found with id {} and employmentInformationId {}", ENTITY_NAME, id, employmentInformationId);
            throw new NotFoundException(id, employmentInformationId, NotFoundException.EntityType.DIVISION_STATION_PLACE_OF_ASSIGNMENT, "employmentInformationId");
        }

        auditUtil.audit(
                AuditAction.VIEW,
                id,
                Optional.empty(),
                redact(
                        data.get(),
                        REDACTED
                ),
                Optional.empty(),
                ENTITY_NAME
        );

        return data
                .map(e -> objectMapper.convertValue(e, DivisionStationPlaceOfAssignmentResponse.class))
                .orElseThrow(() -> new NotFoundException(id, employmentInformationId, NotFoundException.EntityType.DIVISION_STATION_PLACE_OF_ASSIGNMENT, "employmentInformationId"));
    }

    /**
     * Create a new DivisionStationPlaceOfAssignment record associated with an employment information, logging the action in the audit service.
     *
     * @param divisionStationPlaceOfAssignmentRequest The request object containing DivisionStationPlaceOfAssignment details.
     * @param employmentInformationId                 The ID of the associated employment information (used as a fallback if not found in the request).
     * @return DivisionStationPlaceOfAssignmentResponse containing the created DivisionStationPlaceOfAssignment record.
     * @throws NotFoundException if no employment information is found with the given ID from the request or path variable.
     */
    public DivisionStationPlaceOfAssignmentResponse create(
            DivisionStationPlaceOfAssignmentRequest divisionStationPlaceOfAssignmentRequest,
            String employmentInformationId
    ) {
        var employment_information_id_from_request = divisionStationPlaceOfAssignmentRequest.employmentInformationId();
        var optionalEmploymentInformation = employmentInformationRepository.findById(employment_information_id_from_request);

        if (optionalEmploymentInformation.isEmpty()) {
            log.warn("Checking employment information id from request [{}] not found, falling back to path variable [{}]",
                    employment_information_id_from_request,
                    employmentInformationId
            );
            optionalEmploymentInformation = employmentInformationRepository.findById(employmentInformationId);
            if (optionalEmploymentInformation.isEmpty()) {
                throw new NotFoundException(employment_information_id_from_request, NotFoundException.EntityType.DIVISION_STATION_PLACE_OF_ASSIGNMENT);
            }
        }
        var data = objectMapper.convertValue(
                divisionStationPlaceOfAssignmentRepository.saveAndFlush(
                        DivisionStationPlaceOfAssignment.builder()
                                .code(divisionStationPlaceOfAssignmentRequest.code())
                                .name(divisionStationPlaceOfAssignmentRequest.name())
                                .shortName(divisionStationPlaceOfAssignmentRequest.shortName())
                                .employmentInformation(optionalEmploymentInformation.get())
                                .build()
                ),
                DivisionStationPlaceOfAssignmentResponse.class
        );

        auditUtil.audit(
                CREATE,
                divisionStationPlaceOfAssignmentRequest.code(),
                Optional.empty(),
                redact(data, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );
        return data;
    }

    /**
     * Update an existing DivisionStationPlaceOfAssignment record identified by its ID, with optional employment information ID verification, and log the action in the audit service.
     *
     * @param id                                      The ID of the DivisionStationPlaceOfAssignment record to update.
     * @param employmentInformationId                 The ID of the associated employment information (used if {@code useParentIdFromPathVariable} is true).
     * @param divisionStationPlaceOfAssignmentRequest The request object containing updated DivisionStationPlaceOfAssignment details.
     * @param fetchType                               The method to fetch the existing DivisionStationPlaceOfAssignment record (by path variable or with parent path variable).
     * @param useParentIdFromPathVariable             Flag indicating whether to use the employment information ID from the path variable or from the request.
     * @return DivisionStationPlaceOfAssignmentResponse containing the updated DivisionStationPlaceOfAssignment record.
     * @throws NotFoundException if no DivisionStationPlaceOfAssignment record is found with the given criteria.
     */
    public DivisionStationPlaceOfAssignmentResponse update(
            String id,
            String employmentInformationId,
            DivisionStationPlaceOfAssignmentRequest divisionStationPlaceOfAssignmentRequest,
            FetchType fetchType,
            boolean useParentIdFromPathVariable
    ) {
        var divisionPlaceOfAssignmentResponse = switch (fetchType) {
            case BY_PATH_VARIABLE -> findById(id);
            case WITH_PARENT_PATH_VARIABLE ->
                    findByIdAndEmploymentInformationId(id, useParentIdFromPathVariable ? employmentInformationId : divisionStationPlaceOfAssignmentRequest.employmentInformationId());
        };

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(divisionPlaceOfAssignmentResponse, REDACTED)),
                redact(merge(divisionPlaceOfAssignmentResponse, divisionStationPlaceOfAssignmentRequest), REDACTED),
                Optional.of(redact(diff(divisionPlaceOfAssignmentResponse, divisionStationPlaceOfAssignmentRequest), REDACTED)),
                ENTITY_NAME
        );
        return objectMapper.convertValue(
                divisionStationPlaceOfAssignmentRepository.saveAndFlush(
                        objectMapper.convertValue(divisionPlaceOfAssignmentResponse, DivisionStationPlaceOfAssignment.class)
                ),
                DivisionStationPlaceOfAssignmentResponse.class
        );
    }


}
