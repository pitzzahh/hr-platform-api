package dev.araopj.hrplatformapi.employee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.employee.dto.request.DivisionStationPlaceOfAssignmentRequest;
import dev.araopj.hrplatformapi.employee.dto.response.DivisionStationPlaceOfAssignmentResponse;
import dev.araopj.hrplatformapi.employee.model.DivisionStationPlaceOfAssignment;
import dev.araopj.hrplatformapi.employee.repository.DivisionStationPlaceOfAssignmentRepository;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.*;
import dev.araopj.hrplatformapi.utils.enums.CheckType;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.PageRequest;
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
    private final EmploymentInformationRepository employmentInformationRepository;
    private final CommonValidation commonValidation;
    private final ObjectMapper objectMapper;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "employmentInformation");
    private final String ENTITY_NAME = "DivisionStationPlaceOfAssignmentResponse";

    public List<DivisionStationPlaceOfAssignmentResponse> findAll(boolean includeEmploymentInformation, int limit) {

        final var PAGEABLE = PageRequest.of(0, limit);

        final var DATA = divisionStationPlaceOfAssignmentRepository.findAll(PAGEABLE)
                .getContent();

        auditUtil.audit(
                VIEW,
                "[]",
                Optional.empty(),
                Map.of(
                        "timestamp", Instant.now().toString(),
                        "entity", ENTITY_NAME,
                        "count", DATA.size()
                ),
                Optional.empty(),
                ENTITY_NAME
        );

        return DATA.stream()
                .map(entity -> Mapper.toDto(entity, includeEmploymentInformation))
                .toList();
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
                id,
                ENTITY_NAME
        );
        return Optional.of(divisionStationPlaceOfAssignmentRepository.findById(id)
                .map(Mapper::toDto)
                .orElseThrow(() -> new NotFoundException(id, DIVISION_STATION_PLACE_OF_ASSIGNMENT)));
    }

    /**
     * Retrieve a DivisionStationPlaceOfAssignment record by its ID and associated employmentInformation ID, logging the action in the audit service.
     *
     * @param id                      The ID of the DivisionStationPlaceOfAssignment record.
     * @param employmentInformationId The ID of the associated employmentInformation.
     * @return DivisionStationPlaceOfAssignmentResponse containing the DivisionStationPlaceOfAssignment record.
     * @throws NotFoundException() if no DivisionStationPlaceOfAssignment record is found with the given ID and employmentInformation ID.
     */
    public Optional<DivisionStationPlaceOfAssignmentResponse> findByIdAndEmploymentInformationId(String id, String employmentInformationId) {
        final var DATA = divisionStationPlaceOfAssignmentRepository.findByIdAndEmploymentInformation_Id(id, employmentInformationId);

        if (DATA.isEmpty()) {
            log.warn("{} not found with id {} and employmentInformationId {}", ENTITY_NAME, id, employmentInformationId);
            throw new NotFoundException(id, employmentInformationId, DIVISION_STATION_PLACE_OF_ASSIGNMENT, "employmentInformationId");
        }

        auditUtil.audit(
                VIEW,
                id,
                Optional.empty(),
                redact(DATA.get(), REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );

        return Optional.of(DATA
                .map(Mapper::toDto)
                .orElseThrow(() -> new NotFoundException(id, employmentInformationId, DIVISION_STATION_PLACE_OF_ASSIGNMENT, "employmentInformationId")));
    }

    /**
     * Create a new DivisionStationPlaceOfAssignment record associated with an employment information, logging the action in the audit service.
     *
     * @param divisionStationPlaceOfAssignmentRequest The request object containing DivisionStationPlaceOfAssignment details.
     * @param employmentInformationId                 (Optional) The ID of the associated employment information. Required if checkType is CHECK_PARENT_FROM_REQUEST_PARAM.
     * @param checkType                               The method to determine the employment information ID (from request param or request body).
     * @return DivisionStationPlaceOfAssignmentResponse containing the created DivisionStationPlaceOfAssignment record.
     * @throws NotFoundException if no employment information is found with the given ID from the request or path variable.
     */
    public DivisionStationPlaceOfAssignmentResponse create(
            DivisionStationPlaceOfAssignmentRequest divisionStationPlaceOfAssignmentRequest,
            @Nullable String employmentInformationId,
            CheckType checkType
    ) throws BadRequestException {

        final var OPTIONAL_EMPLOYMENT_INFORMATION_CHECK = switch (checkType) {
            case CHECK_PARENT_FROM_REQUEST_PARAM -> {
                if (employmentInformationId == null || employmentInformationId.isEmpty()) {
                    throw new BadRequestException("EmploymentInformation ID must be provided as request parameter when checkType is CHECK_PARENT_FROM_REQUEST_PARAM");
                }
                yield employmentInformationRepository.findById(employmentInformationId);
            }
            case CHECK_PARENT_FROM_REQUEST_BODY -> {
                if (divisionStationPlaceOfAssignmentRequest.employmentInformationId() == null || divisionStationPlaceOfAssignmentRequest.employmentInformationId().isEmpty()) {
                    throw new BadRequestException("EmploymentInformation ID must be provided as request body when checkType is CHECK_PARENT_FROM_REQUEST_BODY");
                }
                yield employmentInformationRepository.findById(divisionStationPlaceOfAssignmentRequest.employmentInformationId());
            }
        };

        final var EMPLOYMENT_INFORMATION_ID_TO_CHECK = getId(divisionStationPlaceOfAssignmentRequest, employmentInformationId, checkType);
        final var RESOLVED_EMPLOYMENT_INFORMATION = OPTIONAL_EMPLOYMENT_INFORMATION_CHECK
                .orElseThrow(() -> new NotFoundException(EMPLOYMENT_INFORMATION_ID_TO_CHECK, NotFoundException.EntityType.EMPLOYMENT_INFORMATION));

        divisionStationPlaceOfAssignmentRepository.findByCodeAndNameAndEmploymentInformationId(
                divisionStationPlaceOfAssignmentRequest.code(),
                divisionStationPlaceOfAssignmentRequest.name(),
                EMPLOYMENT_INFORMATION_ID_TO_CHECK
        ).ifPresent(existing -> {
            throw new IllegalArgumentException("DivisionStationPlaceOfAssignment with code %s and name %s already exists for EmploymentInformation with id %s".formatted(
                    divisionStationPlaceOfAssignmentRequest.code(),
                    divisionStationPlaceOfAssignmentRequest.name(),
                    EMPLOYMENT_INFORMATION_ID_TO_CHECK
            ));
        });

        final var DIVISION_STATION_PLACE_OF_ASSIGNMENT_TO_SAVE = Mapper.toEntity(divisionStationPlaceOfAssignmentRequest);

        auditUtil.audit(
                CREATE,
                String.valueOf(DIVISION_STATION_PLACE_OF_ASSIGNMENT_TO_SAVE.getId()),
                Optional.empty(),
                redact(DIVISION_STATION_PLACE_OF_ASSIGNMENT_TO_SAVE, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );
        return Mapper.toDto(divisionStationPlaceOfAssignmentRepository.save(DIVISION_STATION_PLACE_OF_ASSIGNMENT_TO_SAVE));
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
        if (data.isEmpty()) {
            log.warn("DivisionStationPlaceOfAssignment with id {} and employmentInformationId {} not found for deletion", id, employmentInformationId);
            return false;
        }
        if (!divisionStationPlaceOfAssignmentRepository.existsById(id)) {
            log.warn("DivisionStationPlaceOfAssignment with id {} not found for deletion", id);
            return false;
        }

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

    private static String getId(DivisionStationPlaceOfAssignmentRequest request, String employmentInformationId, CheckType checkType) throws
            BadRequestException {
        switch (checkType) {
            case CHECK_PARENT_FROM_REQUEST_PARAM -> {
                if (employmentInformationId == null || employmentInformationId.isEmpty())
                    throw new BadRequestException("EmploymentInformation ID must be provided as query parameter when checkType CHECK_PARENT_FROM_REQUEST_PARAM.");
                return employmentInformationId;
            }
            case CHECK_PARENT_FROM_REQUEST_BODY -> {
                if (request.employmentInformationId() == null || request.employmentInformationId().isEmpty())
                    throw new BadRequestException("EmploymentInformation ID must be provided in request body when checkType CHECK_PARENT_FROM_REQUEST_BODY.");
                return request.employmentInformationId();
            }
            default -> throw new BadRequestException("Invalid CheckType provided.");
        }
    }

}
