package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.SalaryRequest;
import dev.araopj.hrplatformapi.employee.dto.response.SalaryResponse;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.SalaryRepository;
import dev.araopj.hrplatformapi.employee.service.SalaryService;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.SalaryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.EMPLOYMENT_INFORMATION;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.SALARY;

/**
 * Implementation of the {@link SalaryService} interface.
 * Provides methods for managing salary-related operations such as retrieval, creation, updating, and deletion of salary records.
 *
 * @see SalaryRepository
 * @see SalaryService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryServiceImp implements SalaryService {

    private final EmploymentInformationRepository employmentInformationRepository;
    private final SalaryRepository salaryRepository;

    @Override
    public Page<SalaryResponse> findAll(Pageable pageable) {
        final var SALARY_DATA = salaryRepository.findAll(pageable);

        return SALARY_DATA
                .map(SalaryMapper::toDto);
    }

    @Override
    public Optional<SalaryResponse> findById(String id) {
        return Optional.ofNullable(salaryRepository.findById(id)
                .map(SalaryMapper::toDto)
                .orElseThrow(() -> new NotFoundException(id, SALARY)));
    }

    @Override
    public SalaryResponse create(SalaryRequest salaryRequest) {
        final var EMPLOYMENT_INFORMATION_ID = salaryRequest.employmentInformationId();

        salaryRepository.findByAmountAndCurrencyAndEmploymentInformationId(
                salaryRequest.amount(),
                salaryRequest.currency(),
                EMPLOYMENT_INFORMATION_ID
        ).ifPresent(salary -> {
            throw new IllegalArgumentException("Salary with amount [%s] and currency [%s] already exists for EmploymentInformation with id [%s]".formatted(
                    salary.getAmount(),
                    salary.getCurrency(),
                    EMPLOYMENT_INFORMATION_ID
            ));
        });

        final var SALARY_TO_SAVE = SalaryMapper.toEntity(salaryRequest,
                employmentInformationRepository.findById(EMPLOYMENT_INFORMATION_ID)
                        .orElseThrow(() -> new NotFoundException(EMPLOYMENT_INFORMATION_ID, EMPLOYMENT_INFORMATION))
        );

        return SalaryMapper.toDto(salaryRepository.save(SALARY_TO_SAVE));

    }

    @Override
    public SalaryResponse update(String id, SalaryRequest salaryRequest) throws InvalidRequestException {
        if (id == null || id.isEmpty()) {
            throw new InvalidRequestException("Salary ID must be provided as path");
        }

        final var ORIGINAL_SALARY_DATA = salaryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, SALARY));
        var SALARY_DATA = MergeUtil.merge(ORIGINAL_SALARY_DATA,
                SalaryMapper.toEntity(salaryRequest)
        );

        return SalaryMapper.toDto(salaryRepository.save(SALARY_DATA));
    }

    @Override
    public boolean delete(String id) {
        findById(id).orElseThrow();
        salaryRepository.deleteById(id);
        return !salaryRepository.existsById(id);
    }
}
