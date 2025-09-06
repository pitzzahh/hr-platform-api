package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationResponse;
import dev.araopj.hrplatformapi.employee.repository.EmployeeRepository;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.PositionRepository;
import dev.araopj.hrplatformapi.employee.repository.WorkplaceRepository;
import dev.araopj.hrplatformapi.employee.service.EmploymentInformationService;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.DiffUtil;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.PaginationMeta;
import dev.araopj.hrplatformapi.utils.formatter.DateFormatter;
import dev.araopj.hrplatformapi.utils.mappers.EmploymentInformationMapper;
import dev.araopj.hrplatformapi.utils.mappers.EmploymentInformationSalaryOverrideMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.*;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.*;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

/**
 * Implementation of the {@link EmploymentInformationService} interface.
 * This service provides methods for managing employment information records,
 * including retrieval, creation, updating, and deletion operations.
 *
 * @see EmploymentInformationService
 * @see EmploymentInformationResponse
 * @see EmploymentInformationRequest
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentInformationServiceImp implements EmploymentInformationService {

    private final EmploymentInformationRepository employmentInformationRepository;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final WorkplaceRepository workplaceRepository;

    private final EmploymentInformationMapper employmentInformationMapper;
    private final EmploymentInformationSalaryOverrideMapper employmentInformationSalaryOverrideMapper;

    private final AuditUtil auditUtil;
    private final DateFormatter dateFormatter;
    private final Set<String> REDACTED = Set.of("employeeResponse", "employmentInformationSalaryOverrideResponse", "positionResponse", "workplaceResponse");
    private final String ENTITY_NAME = EmploymentInformationResponse.class.getName();

    @Override
    public Page<EmploymentInformationResponse> findAll(Pageable pageable) {
        final var PAGINATED_DATA = employmentInformationRepository.findAll(pageable);
        auditUtil.audit(
                VIEW,
                "[]",
                Optional.of(PaginationMeta.builder()
                        .totalElements(PAGINATED_DATA.getTotalElements())
                        .size(PAGINATED_DATA.getSize())
                        .page(PAGINATED_DATA.getNumber() + 1)
                        .totalPages(PAGINATED_DATA.getTotalPages())
                        .build()),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );

        return PAGINATED_DATA
                .map(employmentInformation -> employmentInformationMapper.toDto(employmentInformation, false));
    }

    @Override
    public Page<EmploymentInformationResponse> findByEmployeeId(String employeeId, Pageable pageable) {
        final var PAGINATED_DATA = employmentInformationRepository.findByEmployeeId(employeeId, pageable);
        auditUtil.audit(
                VIEW,
                "[]",
                Optional.of(PaginationMeta.builder()
                        .totalElements(PAGINATED_DATA.getTotalElements())
                        .size(PAGINATED_DATA.getSize())
                        .page(PAGINATED_DATA.getNumber() + 1)
                        .totalPages(PAGINATED_DATA.getTotalPages())
                        .build()),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );

        return PAGINATED_DATA
                .map(employmentInformation -> employmentInformationMapper.toDto(employmentInformation, false));
    }

    @Override
    public Optional<EmploymentInformationResponse> findById(String id) {
        auditUtil.audit(
                id,
                ENTITY_NAME
        );
        return Optional.ofNullable(employmentInformationRepository.findById(id)
                .map(employmentInformation -> employmentInformationMapper.toDto(employmentInformation, false))
                .orElseThrow(() -> new NotFoundException(id, EMPLOYMENT_INFORMATION)));
    }

    @Override
    public EmploymentInformationResponse create(String id, EmploymentInformationRequest employmentInformationRequest) {
        final var EMPLOYEE_ID = employmentInformationRequest.employeeId();
        final var POSITION_ID = employmentInformationRequest.positionId();
        final var WORKPLACE_ID = employmentInformationRequest.workplaceId();

        final var NEW_SALARY_OVERRIDE = employmentInformationSalaryOverrideMapper.toEntity(
                employmentInformationRequest.employmentInformationSalaryOverrideRequest()
        );
        final var EXISTING_EMPLOYEE = employeeRepository.findById(EMPLOYEE_ID)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID, EMPLOYEE));
        final var EXISTING_POSITION = positionRepository.findById(POSITION_ID)
                .orElseThrow(() -> new NotFoundException(POSITION_ID, POSITION));
        final var EXISTING_WORKPLACE = workplaceRepository.findById(WORKPLACE_ID)
                .orElseThrow(() -> new NotFoundException(WORKPLACE_ID, WORKPLACE));

        employmentInformationRepository.findByStartDateAndEndDateAndRemarksAndEmployeeId(
                employmentInformationRequest.startDate(),
                employmentInformationRequest.endDate(),
                employmentInformationRequest.remarks(),
                EMPLOYEE_ID

        ).ifPresent(workplace -> {
            throw new IllegalArgumentException("Workplace with start date [%s], end date [%s], and remarks [%s] already exists for Employee with id [%s]".formatted(
                    dateFormatter.format(workplace.getStartDate(), "long"),
                    dateFormatter.format(workplace.getEndDate(), "long"),
                    workplace.getRemarks(),
                    EMPLOYEE_ID
            ));
        });

        final var WORKPLACE_TO_SAVE = employmentInformationMapper.toEntity(employmentInformationRequest,
                EXISTING_EMPLOYEE,
                NEW_SALARY_OVERRIDE,
                EXISTING_POSITION,
                EXISTING_WORKPLACE
        );

        auditUtil.audit(
                CREATE,
                WORKPLACE_TO_SAVE.getId(),
                Optional.empty(),
                redact(WORKPLACE_TO_SAVE, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );

        return employmentInformationMapper.toDto(employmentInformationRepository.save(WORKPLACE_TO_SAVE), false);
    }

    @Override
    public EmploymentInformationResponse update(String id, EmploymentInformationRequest employmentInformationRequest) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("EmploymentInformation ID must be provided as path");
        }

        final var ORIGINAL_EMPLOYMENT_INFORMATION = employmentInformationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, EMPLOYMENT_INFORMATION));

        var WORKPLACE_DATA = MergeUtil.merge(ORIGINAL_EMPLOYMENT_INFORMATION,
                employmentInformationMapper.toEntity(employmentInformationRequest,
                        employmentInformationSalaryOverrideMapper.toEntity(employmentInformationRequest.employmentInformationSalaryOverrideRequest())
                )
        );

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(ORIGINAL_EMPLOYMENT_INFORMATION, REDACTED)),
                redact(WORKPLACE_DATA, REDACTED),
                Optional.of(redact(DiffUtil.diff(ORIGINAL_EMPLOYMENT_INFORMATION, WORKPLACE_DATA), REDACTED)),
                ENTITY_NAME
        );

        return employmentInformationMapper.toDto(employmentInformationRepository.save(WORKPLACE_DATA), false);

    }

    @Override
    public boolean delete(String id) {
        findById(id).orElseThrow();
        auditUtil.audit(
                DELETE,
                id,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );
        employmentInformationRepository.deleteById(id);
        return true;
    }
}
