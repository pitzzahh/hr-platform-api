package dev.araopj.hrplatformapi.employee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.audit.model.AuditAction;
import dev.araopj.hrplatformapi.employee.dto.request.DivisionStationPlaceOfAssignmentRequest;
import dev.araopj.hrplatformapi.employee.dto.response.DivisionStationPlaceOfAssignmentResponse;
import dev.araopj.hrplatformapi.employee.model.DivisionStationPlaceOfAssignment;
import dev.araopj.hrplatformapi.employee.repository.DivisionStationPlaceOfAssignmentRepository;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.*;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.DIVISION_STATION_PLACE_OF_ASSIGNMENT;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

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
 * @see DivisionStationPlaceOfAssignmentRepository
 * @see CommonValidation
 * @see AuditUtil
 * @see ObjectMapper
 * @since 0.0.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DivisionStationPlaceOfAssignmentService {

    private final DivisionStationPlaceOfAssignmentRepository divisionStationPlaceOfAssignmentRepository;
    private final CommonValidation commonValidation;
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
                .orElseThrow(() -> new NotFoundException(id, DIVISION_STATION_PLACE_OF_ASSIGNMENT)));

    }

    /**
     * Retrieve a DivisionStationPlaceOfAssignment record by its ID and associated employee ID, logging the action in the audit service.
     *
     * @param id                      The ID of the DivisionStationPlaceOfAssignment record.
     * @param employmentInformationId The ID of the associated employee.
     * @return DivisionStationPlaceOfAssignmentResponse containing the DivisionStationPlaceOfAssignment record.
     * @throws NotFoundException() if no DivisionStationPlaceOfAssignment record is found with the given ID and employee ID.
     */
    public Optional<DivisionStationPlaceOfAssignmentResponse> findByIdAndEmploymentInformationId(String id, String employmentInformationId) {
        var data = divisionStationPlaceOfAssignmentRepository.findByIdAndEmploymentInformation_Id(id, employmentInformationId);

        if (data.isEmpty()) {
            log.warn("{} not found with id {} and employmentInformationId {}", ENTITY_NAME, id, employmentInformationId);
            throw new NotFoundException(id, employmentInformationId, DIVISION_STATION_PLACE_OF_ASSIGNMENT, "employmentInformationId");
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

        return Optional.of(data
                .map(Mapper::toDto)
                .orElseThrow(() -> new NotFoundException(id, employmentInformationId, DIVISION_STATION_PLACE_OF_ASSIGNMENT, "employmentInformationId")));
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
        var optionalEmploymentInformation = commonValidation.validateEmploymentInformationExists(
                divisionStationPlaceOfAssignmentRequest.employmentInformationId(),
                employmentInformationId,
                DIVISION_STATION_PLACE_OF_ASSIGNMENT
        );
        var data = objectMapper.convertValue(
                divisionStationPlaceOfAssignmentRepository.saveAndFlush(
                        DivisionStationPlaceOfAssignment.builder()
                                .code(divisionStationPlaceOfAssignmentRequest.code())
                                .name(divisionStationPlaceOfAssignmentRequest.name())
                                .shortName(divisionStationPlaceOfAssignmentRequest.shortName())
                                .employmentInformation(optionalEmploymentInformation)
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
     * Update an existing {@link DivisionStationPlaceOfAssignment} record identified by its ID, with optional employment information ID verification, and log the action in the audit utility.
     *
     * @param id                                      The ID of the {@link DivisionStationPlaceOfAssignment} record to update.
     * @param divisionStationPlaceOfAssignmentRequest The request object containing updated DivisionStationPlaceOfAssignment details.
     * @return {@link DivisionStationPlaceOfAssignmentResponse} containing the updated DivisionStationPlaceOfAssignment record.
     * @throws NotFoundException if no DivisionStationPlaceOfAssignment record is found with the given criteria.
     */
    public DivisionStationPlaceOfAssignmentResponse update(
            String id,
            DivisionStationPlaceOfAssignmentRequest divisionStationPlaceOfAssignmentRequest
    ) throws BadRequestException {

        if (id == null || id.isEmpty()) {
            throw new BadRequestException("DivisionStationPlaceOfAssignment ID must be provided as path");
        }

        final var EMPLOYMENT_INFORMATION_ID = divisionStationPlaceOfAssignmentRequest.employmentInformationId();

        final var ORIGINAL_DIVISION_STATION_PLACE_OF_ASSIGNMENT = divisionStationPlaceOfAssignmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, DIVISION_STATION_PLACE_OF_ASSIGNMENT));
        final var DIVISION_STATION_PLACE_OF_ASSIGNMENT_DATA = MergeUtil.<DivisionStationPlaceOfAssignment>merge(
                ORIGINAL_DIVISION_STATION_PLACE_OF_ASSIGNMENT,
                Mapper.toEntity(divisionStationPlaceOfAssignmentRequest)
        );

        if (EMPLOYMENT_INFORMATION_ID != null && !EMPLOYMENT_INFORMATION_ID.isEmpty()) {
            final var EMPLOYMENT_INFORMATION_PARENT = findByIdAndEmploymentInformationId(id, EMPLOYMENT_INFORMATION_ID)
                    .orElseThrow(() -> new NotFoundException(id, EMPLOYMENT_INFORMATION_ID, DIVISION_STATION_PLACE_OF_ASSIGNMENT, "employmentInformationId"));

            if (!Objects.equals(EMPLOYMENT_INFORMATION_PARENT.id(), DIVISION_STATION_PLACE_OF_ASSIGNMENT_DATA.getEmploymentInformation().getId())) {
                throw new BadRequestException("DivisionStationPlaceOfAssignment with id %s does not belong to EmploymentInformation with id %s".formatted(id, EMPLOYMENT_INFORMATION_ID));
            }

            divisionStationPlaceOfAssignmentRepository.findByCodeAndNameAndEmploymentInformationId(
                    divisionStationPlaceOfAssignmentRequest.code(),
                    divisionStationPlaceOfAssignmentRequest.name(),
                    EMPLOYMENT_INFORMATION_ID
            ).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("DivisionStationPlaceOfAssignment with code %s and name %s already exists for EmploymentInformation with id %s".formatted(
                            divisionStationPlaceOfAssignmentRequest.code(),
                            divisionStationPlaceOfAssignmentRequest.name(),
                            EMPLOYMENT_INFORMATION_ID
                    ));
                }
            });
        }

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(ORIGINAL_DIVISION_STATION_PLACE_OF_ASSIGNMENT, REDACTED)),
                redact(DIVISION_STATION_PLACE_OF_ASSIGNMENT_DATA, REDACTED),
                Optional.of(redact(DiffUtil.diff(ORIGINAL_DIVISION_STATION_PLACE_OF_ASSIGNMENT, DIVISION_STATION_PLACE_OF_ASSIGNMENT_DATA), REDACTED)),
                ENTITY_NAME
        );

        return Mapper.toDto(divisionStationPlaceOfAssignmentRepository.saveAndFlush(DIVISION_STATION_PLACE_OF_ASSIGNMENT_DATA));

    }

    /**
     * Delete a DivisionStationPlaceOfAssignment record by its ID and associated employment information ID, logging the action in the audit service.
     *
     * @param id                      The ID of the DivisionStationPlaceOfAssignment record to delete.
     * @param employmentInformationId The ID of the associated employment information.
     * @return true if the deletion was successful.
     * @throws NotFoundException if no DivisionStationPlaceOfAssignment record is found with the given ID and employment information ID.
     */
    public boolean delete(String id, String employmentInformationId) {
        var data = findByIdAndEmploymentInformationId(id, employmentInformationId);

        divisionStationPlaceOfAssignmentRepository.deleteById(id);

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
