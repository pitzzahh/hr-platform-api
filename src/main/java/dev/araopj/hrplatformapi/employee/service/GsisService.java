package dev.araopj.hrplatformapi.employee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.audit.dto.AuditDto;
import dev.araopj.hrplatformapi.audit.model.AuditAction;
import dev.araopj.hrplatformapi.audit.service.AuditService;
import dev.araopj.hrplatformapi.employee.dto.request.GsisRequest;
import dev.araopj.hrplatformapi.employee.dto.response.GsisResponse;
import dev.araopj.hrplatformapi.employee.model.Gsis;
import dev.araopj.hrplatformapi.employee.repository.EmployeeRepository;
import dev.araopj.hrplatformapi.employee.repository.GsisRepository;
import dev.araopj.hrplatformapi.exception.GsisNotFoundException;
import dev.araopj.hrplatformapi.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class GsisService {

    private final GsisRepository gsisRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    private final Set<String> redacted = Set.of("id", "businessPartnerNumber", "employee");

    /**
     * Retrieve all GSIS records and log the action in the audit service.
     *
     * @return List of GsisResponse containing all GSIS records.
     */
    public List<GsisResponse> findAll() {
        var data = gsisRepository.findAll()
                .stream()
                .map(Mapper::toDto)
                .toList();

        audit(
                AuditAction.VIEW,
                "[]",
                Optional.empty(),
                Map.of(
                        "timestamp", Instant.now().toString(),
                        "entity", "Gsis",
                        "count", data.size()
                ),
                Optional.empty()
        );
        return data;
    }

    /**
     * Retrieve a GSIS record by its ID and log the action in the audit service.
     *
     * @param id The ID of the GSIS record.
     * @return GsisResponse containing the GSIS record.
     * @throws GsisNotFoundException if no GSIS record is found with the given ID.
     */
    public GsisResponse findById(String id) {
        audit(
                AuditAction.VIEW,
                "[]",
                Optional.empty(),
                Map.of("timestamp", Instant.now().toString(),
                        "entity", "Gsis",
                        "request_id", id),
                Optional.empty()
        );
        return gsisRepository.findById(id)
                .map(Mapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Gsis not found with id {}", id);
                    return new GsisNotFoundException(id);
                });

    }

    /**
     * Retrieve a GSIS record by its ID and associated employee ID, logging the action in the audit service.
     *
     * @param id         The ID of the GSIS record.
     * @param employeeId The ID of the associated employee.
     * @return GsisResponse containing the GSIS record.
     * @throws GsisNotFoundException if no GSIS record is found with the given ID and employee ID.
     */
    public GsisResponse findByIdAndEmployeeId(String id, String employeeId) {
        var data = gsisRepository.findByIdAndEmployee_Id(id, employeeId);

        if (data.isEmpty()) {
            log.warn("Gsis not found with id {} and employeeId {}", id, employeeId);
            throw new GsisNotFoundException(id);
        }

        audit(
                AuditAction.VIEW,
                id,
                Optional.empty(),
                JsonRedactor.redact(
                        data.get(),
                        redacted
                ),
                Optional.empty()
        );

        return data.map(Mapper::toDto).orElseThrow(() -> new GsisNotFoundException(id, employeeId));
    }

    /**
     * Create a new GSIS record associated with an employee, logging the action in the audit service.
     *
     * @param gsisRequest The request object containing GSIS details.
     * @param employeeId  The ID of the associated employee (used as a fallback if not found in the request).
     * @return GsisResponse containing the created GSIS record.
     * @throws GsisNotFoundException if no employee is found with the given ID from the request or path variable.
     */
    public GsisResponse create(
            GsisRequest gsisRequest,
            String employeeId
    ) {
        var employee_id_from_request = gsisRequest.getEmployeeId();
        var optionalEmployee = employeeRepository.findById(employee_id_from_request);

        if (optionalEmployee.isEmpty()) {
            log.warn("Checking employee id from request [{}] not found, falling back to path variable [{}]",
                    employee_id_from_request,
                    employeeId
            );
            optionalEmployee = employeeRepository.findById(employeeId);
            if (optionalEmployee.isEmpty()) {
                throw new GsisNotFoundException(employee_id_from_request);
            }
        }
        var data = Mapper.toDto(gsisRepository.saveAndFlush(
                Gsis.builder()
                        .businessPartnerNumber(gsisRequest.getBusinessPartnerNumber())
                        .issuedDate(gsisRequest.getIssuedDate())
                        .issuedPlace(gsisRequest.getIssuedPlace())
                        .employee(optionalEmployee.get())
                        .build()
        ));

        audit(
                AuditAction.CREATE,
                gsisRequest.getBusinessPartnerNumber(),
                Optional.empty(),
                JsonRedactor.redact(data, redacted),
                Optional.empty()
        );
        return data;
    }

    /**
     * Update an existing GSIS record identified by its ID, with optional employee ID verification, and log the action in the audit service.
     *
     * @param id                            The ID of the GSIS record to update.
     * @param employeeId                    The ID of the associated employee (used if {@code useParentIdFromPathVariable} is true).
     * @param gsisRequest                   The request object containing updated GSIS details.
     * @param fetchType                     The method to fetch the existing GSIS record (by path variable or with parent path variable).
     * @param useParentIdFromPathVariable   Flag indicating whether to use the employee ID from the path variable or from the request.
     * @return GsisResponse containing the updated GSIS record.
     * @throws GsisNotFoundException if no GSIS record is found with the given criteria.
     */
    public GsisResponse update(
            String id,
            String employeeId,
            GsisRequest gsisRequest,
            FetchType fetchType,
            boolean useParentIdFromPathVariable
    ) {
        var gsisResponse = switch (fetchType) {
            case BY_PATH_VARIABLE -> findById(id);
            case WITH_PARENT_PATH_VARIABLE ->
                    findByIdAndEmployeeId(id, useParentIdFromPathVariable ? employeeId : gsisRequest.getEmployeeId());
        };

        audit(
                AuditAction.UPDATE,
                id,
                Optional.of(JsonRedactor.redact(gsisResponse, redacted)),
                JsonRedactor.redact(MergeUtil.merge(gsisResponse, gsisRequest), redacted),
                Optional.of(JsonRedactor.redact(DiffUtil.diff(gsisResponse, gsisRequest), redacted))
        );
        return Mapper.toDto(
                gsisRepository.saveAndFlush(
                        Mapper.toEntity(gsisResponse)
                )
        );
    }

    /**
     * Logs an audit event with the provided details.
     *
     * @param action   The audit action performed (e.g., {@link dev.araopj.hrplatformapi.audit.model.AuditAction#CREATE}, {@link dev.araopj.hrplatformapi.audit.model.AuditAction#UPDATE}, {@link dev.araopj.hrplatformapi.audit.model.AuditAction#VIEW}).
     * @param entityId The identifier of the entity being audited.
     * @param oldData  Optional previous state of the entity (before the action), see {@link java.util.Optional}.
     * @param newData  The new state of the entity (after the action).
     * @param changes  Optional object representing the changes between old and new data, see {@link java.util.Optional}.
     * @see dev.araopj.hrplatformapi.audit.model.AuditAction
     * @see dev.araopj.hrplatformapi.audit.dto.AuditDto
     * @see dev.araopj.hrplatformapi.audit.service.AuditService
     */
    private void audit(AuditAction action, String entityId, Optional<Object> oldData, Object newData, Optional<Object> changes) {
        var builder = AuditDto.builder()
                .action(action)
                .newData(objectMapper.valueToTree(newData))
                .performedBy("system")
                .entityType("Gsis")
                .entityId(entityId);

        oldData.ifPresent(o -> builder.oldData(objectMapper.valueToTree(o)));
        changes.ifPresent(c -> builder.changes(objectMapper.valueToTree(c)));

        auditService.create(builder.build());
    }

}
