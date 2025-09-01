package dev.araopj.hrplatformapi.employee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.audit.model.AuditAction;
import dev.araopj.hrplatformapi.employee.dto.response.DivisionStationPlaceOfAssignmentResponse;
import dev.araopj.hrplatformapi.employee.model.DivisionStationPlaceOfAssignment;
import dev.araopj.hrplatformapi.employee.repository.DivisionStationPlaceOfAssignmentRepository;
import dev.araopj.hrplatformapi.employee.repository.EmployeeRepository;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.AuditUtil;
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
public class DivisionStationPlaceOfAssignmentService {

    private final DivisionStationPlaceOfAssignmentRepository divisionStationPlaceOfAssignmentRepository;
    private final EmployeeRepository employeeRepository;
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
                .orElseThrow(() -> new NotFoundException(id, NotFoundException.EntityType.GSIS)));

    }

}
