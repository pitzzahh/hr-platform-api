package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationSalaryOverrideRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationSalaryOverrideResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformationSalaryOverride;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationSalaryOverrideRepository;
import dev.araopj.hrplatformapi.employee.service.EmploymentInformationSalaryOverrideService;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.CommonValidation;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.EmploymentInformationSalaryOverrideMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.EMPLOYMENT_INFORMATION;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.EMPLOYMENT_INFORMATION_SALARY_OVERRIDE;

/**
 * Service class for managing EmploymentInformationSalaryOverride entities.
 * <p>
 * This service provides methods to create, read, update, and delete EmploymentInformationSalaryOverride records.
 *
 * @see EmploymentInformationSalaryOverride
 * @see EmploymentInformationSalaryOverrideRepository
 * @see CommonValidation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentInformationSalaryOverrideServiceImp implements EmploymentInformationSalaryOverrideService {

    private final EmploymentInformationSalaryOverrideRepository employmentInformationSalaryOverrideRepository;
    private final EmploymentInformationRepository employmentInformationRepository;
    private final EmploymentInformationSalaryOverrideMapper employmentInformationSalaryOverrideMapper;

    @Override
    public List<EmploymentInformationSalaryOverrideResponse> findAll() {
        var EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_DATA = employmentInformationSalaryOverrideRepository.findAll();

        return EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_DATA
                .stream()
                .map(employmentInformationSalaryOverrideMapper::toDto)
                .toList();
    }

    @Override
    public Optional<EmploymentInformationSalaryOverrideResponse> findById(String id) {
        return Optional.of(employmentInformationSalaryOverrideRepository.findById(id)
                .map(employmentInformationSalaryOverrideMapper::toDto)
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

        return employmentInformationSalaryOverrideMapper.toDto(employmentInformationSalaryOverrideRepository.save(EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_TO_SAVE));
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
                employmentInformationSalaryOverrideMapper.toEntity(employmentInformationSalaryOverrideRequest)
        );

        return employmentInformationSalaryOverrideMapper.toDto(
                employmentInformationSalaryOverrideRepository.save(EMPLOYMENT_INFORMATION_SALARY_OVERRIDE_DATA)
        );
    }

    @Override
    public boolean delete(String id) {
        findById(id).orElseThrow();
        employmentInformationSalaryOverrideRepository.deleteById(id);
        return !employmentInformationSalaryOverrideRepository.existsById(id);
    }
}
