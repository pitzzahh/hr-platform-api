package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationSalaryOverrideRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationSalaryOverrideResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformationSalaryOverride;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationSalaryOverrideRepository;
import dev.araopj.hrplatformapi.employee.service.IEmploymentInformationSalaryOverrideService;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.*;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.EMPLOYMENT_INFORMATION;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.EMPLOYMENT_INFORMATION_SALARY_OVERRIDE;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

/**
 * Service class for managing EmploymentInformationSalaryOverride entities.
 * <p>
 * This service provides methods to create, read, update, and delete EmploymentInformationSalaryOverride records.
 * It also integrates with the AuditUtil to log actions performed on these records.
 *
 * @see EmploymentInformationSalaryOverride
 * @see EmploymentInformationSalaryOverrideRepository
 * @see CommonValidation
 * @see AuditUtil
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentInformationSalaryOverrideServiceImp implements IEmploymentInformationSalaryOverrideService {

    private final EmploymentInformationSalaryOverrideRepository employmentInformationSalaryOverrideRepository;
    private final EmploymentInformationRepository employmentInformationRepository;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("effectiveDate", "employmentInformation");
    private final String ENTITY_NAME = "EmploymentInformationSalaryOverride";

    @Override
    public List<EmploymentInformationSalaryOverrideResponse> findAll() {
        var EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_DATA = employmentInformationSalaryOverrideRepository.findAll();
        auditUtil.audit(
                VIEW,
                "[]",
                Optional.empty(),
                Map.of(
                        "timestamp", Instant.now().toString(),
                        "entity", ENTITY_NAME,
                        "count", EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_DATA.size()
                ),
                Optional.empty(),
                ENTITY_NAME
        );

        return EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_DATA
                .stream()
                .map(Mapper::toDto)
                .toList();
    }

    @Override
    public Optional<EmploymentInformationSalaryOverrideResponse> findById(String id) {
        auditUtil.audit(
                id,
                ENTITY_NAME
        );
        return Optional.of(employmentInformationSalaryOverrideRepository.findById(id)
                .map(Mapper::toDto)
                .orElseThrow(() -> new NotFoundException(id, EMPLOYMENT_INFORMATION_SALARY_OVERRIDE)));
    }

    @Override
    public EmploymentInformationSalaryOverrideResponse create(
            EmploymentInformationSalaryOverrideRequest employmentInformationSalaryOverrideRequest
    ) {

        final var EMPLOYMENT_INFORMATION_ID = employmentInformationSalaryOverrideRequest.employmentInformationId();
        employmentInformationSalaryOverrideRepository.findBySalaryAndEffectiveDateAndEmploymentInformationId(
                employmentInformationSalaryOverrideRequest.salary(),
                employmentInformationSalaryOverrideRequest.effectiveDate(),
                EMPLOYMENT_INFORMATION_ID
        ).ifPresent(existing -> {
                    throw new IllegalArgumentException(String.format(
                            "EmploymentInformationSalaryOverride already exists with salary %s, effectiveDate %s, for employment information with id [%s]",
                            existing.getSalary(),
                            existing.getEffectiveDate(),
                            EMPLOYMENT_INFORMATION_ID
                    ));
                }
        );

        final var EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_TO_SAVE = EmploymentInformationSalaryOverride.builder()
                .salary(employmentInformationSalaryOverrideRequest.salary())
                .effectiveDate(employmentInformationSalaryOverrideRequest.effectiveDate())
                .employmentInformation(
                        employmentInformationRepository
                                .findById(EMPLOYMENT_INFORMATION_ID)
                                .orElseThrow(() -> new NotFoundException(EMPLOYMENT_INFORMATION_ID, EMPLOYMENT_INFORMATION))
                )
                .build();

        auditUtil.audit(
                CREATE,
                EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_TO_SAVE.getId(),
                Optional.empty(),
                redact(EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_TO_SAVE, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );

        return Mapper.toDto(employmentInformationSalaryOverrideRepository.save(EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_TO_SAVE));
    }

    @Override
    public EmploymentInformationSalaryOverrideResponse update(
            String id,
            EmploymentInformationSalaryOverrideRequest.WithoutEmploymentInformationId employmentInformationSalaryOverrideRequest
    ) throws BadRequestException {

        if (id == null || id.isEmpty()) {
            throw new BadRequestException("EmploymentInformationSalaryOverride ID must be provided as path");
        }

        final var ORIGINAL_EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_DATA = employmentInformationSalaryOverrideRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(id, EMPLOYMENT_INFORMATION_SALARY_OVERRIDE));

        var EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_DATA = MergeUtil.merge(
                ORIGINAL_EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_DATA,
                Mapper.toEntity(employmentInformationSalaryOverrideRequest)
        );

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(ORIGINAL_EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_DATA, REDACTED)),
                redact(EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_DATA, REDACTED),
                Optional.of(redact(DiffUtil.diff(ORIGINAL_EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_DATA, EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_DATA), REDACTED)),
                ENTITY_NAME
        );

        return Mapper.toDto(employmentInformationSalaryOverrideRepository.save(EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_DATA));
    }

    @Override
    public boolean delete(String id) {
        var data = findById(id);
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
