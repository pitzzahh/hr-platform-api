package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationResponse;
import dev.araopj.hrplatformapi.employee.repository.*;
import dev.araopj.hrplatformapi.employee.service.EmploymentInformationService;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.formatter.DateFormatter;
import dev.araopj.hrplatformapi.utils.mappers.EmploymentInformationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentInformationServiceImp implements EmploymentInformationService {

    private final EmploymentInformationRepository employmentInformationRepository;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final WorkplaceRepository workplaceRepository;
    private final SalaryRepository salaryRepository;

    @Override
    public Page<EmploymentInformationResponse> findAll(Pageable pageable) {
        final var PAGINATED_DATA = employmentInformationRepository.findAll(pageable);

        return PAGINATED_DATA
                .map(employmentInformation -> EmploymentInformationMapper.toDto(
                        employmentInformation,
                        false
                ));
    }

    @Override
    public Page<EmploymentInformationResponse> findByEmployeeId(String employeeId, Pageable pageable) {
        if (employeeId == null || employeeId.isEmpty()) {
            throw new InvalidRequestException("Employee ID must be provided");
        }
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(employeeId, EMPLOYEE));

        final var PAGINATED_DATA = employmentInformationRepository.findByEmployeeId(employeeId, pageable);

        return PAGINATED_DATA
                .map(employmentInformation -> EmploymentInformationMapper.toDto(
                        employmentInformation,
                        false
                ));
    }

    @Override
    public Optional<EmploymentInformationResponse> findById(String id) {
        if (id == null || id.isEmpty()) {
            throw new InvalidRequestException("EmploymentInformation ID must be provided");
        }
        return Optional.ofNullable(employmentInformationRepository.findById(id)
                .map(employmentInformation -> EmploymentInformationMapper.toDto(
                        employmentInformation,
                        false
                ))
                .orElseThrow(() -> new NotFoundException(id, EMPLOYMENT_INFORMATION)));
    }

    @Override
    public EmploymentInformationResponse create(String id, EmploymentInformationRequest employmentInformationRequest) throws InvalidRequestException, NotFoundException {
        final var EMPLOYEE_ID = employmentInformationRequest.employeeId();
        final var POSITION_ID = employmentInformationRequest.positionId();
        final var WORKPLACE_ID = employmentInformationRequest.workplaceId();
        final var EXISTING_SALARY_ID = employmentInformationRequest.salaryId();

        final var EXISTING_EMPLOYEE = employeeRepository.findById(EMPLOYEE_ID)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID, EMPLOYEE));
        final var EXISTING_POSITION = positionRepository.findById(POSITION_ID)
                .orElseThrow(() -> new NotFoundException(POSITION_ID, POSITION));
        final var EXISTING_WORKPLACE = workplaceRepository.findById(WORKPLACE_ID)
                .orElseThrow(() -> new NotFoundException(WORKPLACE_ID, WORKPLACE));
        final var EXISTING_SALARY = salaryRepository.findById(EXISTING_SALARY_ID)
                .orElseThrow(() -> new NotFoundException(EXISTING_SALARY_ID, SALARY));


        employmentInformationRepository.findByStartDateAndEndDateAndRemarksAndEmployeeId(
                employmentInformationRequest.startDate(),
                employmentInformationRequest.endDate(),
                employmentInformationRequest.remarks(),
                EMPLOYEE_ID
        ).ifPresent(workplace -> {
            throw new InvalidRequestException("Workplace with start date [%s], end date [%s], and remarks [%s] already exists for Employee with id [%s]".formatted(
                    DateFormatter.format(workplace.getStartDate(), "long"),
                    DateFormatter.format(workplace.getEndDate(), "long"),
                    workplace.getRemarks(),
                    EMPLOYEE_ID
            ));
        });

        final var WORKPLACE_TO_SAVE = EmploymentInformationMapper.toEntity(employmentInformationRequest,
                EXISTING_EMPLOYEE,
                EXISTING_SALARY,
                EXISTING_POSITION,
                EXISTING_WORKPLACE
        );

        return EmploymentInformationMapper.toDto(
                employmentInformationRepository.save(WORKPLACE_TO_SAVE),
                false
        );
    }

    @Override
    public EmploymentInformationResponse update(String id, EmploymentInformationRequest employmentInformationRequest) throws InvalidRequestException {
        if (id == null || id.isEmpty()) {
            throw new InvalidRequestException("Employee ID must be provided as path");
        }

        final var ORIGINAL_EMPLOYMENT_INFORMATION = employmentInformationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, EMPLOYMENT_INFORMATION));

        var WORKPLACE_DATA = MergeUtil.merge(ORIGINAL_EMPLOYMENT_INFORMATION,
                EmploymentInformationMapper.toEntity(employmentInformationRequest)
        );

        return EmploymentInformationMapper.toDto(
                employmentInformationRepository.save(WORKPLACE_DATA),
                false
        );
    }

    @Override
    public boolean delete(String id) {
        findById(id).orElseThrow();
        employmentInformationRepository.deleteById(id);
        return !employmentInformationRepository.existsById(id);
    }
}