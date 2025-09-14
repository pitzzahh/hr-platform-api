package dev.araopj.hrplatformapi.salary.service.impl;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.salary.service.SalaryGradeService;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.SalaryDataMapper;
import dev.araopj.hrplatformapi.utils.mappers.SalaryGradeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryGradeServiceImp implements SalaryGradeService {

    private final SalaryGradeRepository salaryGradeRepository;
    private final SalaryGradeMapper salaryGradeMapper;
    private final SalaryDataMapper salaryDataMapper;

    @Override
    public Page<SalaryGradeResponse> findAll(Pageable pageable, boolean includeSalaryData) {
        final var SALARY_GRADES = includeSalaryData ?
                salaryGradeRepository.findAllWithSalaryData(pageable) : salaryGradeRepository.findAll(pageable);
        return SALARY_GRADES
                .map(entity -> salaryGradeMapper.toDto(entity, includeSalaryData));
    }

    @Override
    public Optional<SalaryGradeResponse> findById(String id, boolean includeSalaryData) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("SalaryGrade ID must be provided as path");
        }
        final var SALARY_GRADE = includeSalaryData ?
                salaryGradeRepository.findSalaryGradeWithSalaryDataById(id)
                        .orElseThrow(() -> new NotFoundException(id, NotFoundException.EntityType.SALARY_GRADE)) :
                salaryGradeRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(id, NotFoundException.EntityType.SALARY_GRADE));

        return Optional.ofNullable(salaryGradeMapper.toDto(SALARY_GRADE, includeSalaryData));
    }

    @Override
    public List<SalaryGradeResponse> create(
            List<SalaryGradeRequest> salaryGradeRequests,
            boolean includeSalaryData
    ) throws BadRequestException {
        if (salaryGradeRequests.isEmpty()) {
            return new ArrayList<>();
        }

        // Validate all requests first
        salaryGradeRequests.forEach(request -> validateSalaryGradeExistence(includeSalaryData, request));

        return salaryGradeRepository.saveAll(salaryGradeRequests.stream()
                        .map(request -> {
                            SalaryGrade salaryGrade = salaryGradeMapper.toEntity(request);

                            if (includeSalaryData && request.salaryData() != null) {
                                salaryGrade.setSalaryData(
                                        request.salaryData().stream()
                                                .map(e -> {
                                                    var salaryData = salaryDataMapper.toEntity(e);
                                                    salaryData.setSalaryGrade(salaryGrade);
                                                    return salaryData;
                                                })
                                                .toList()
                                );
                            }
                            return salaryGrade;
                        })
                        .toList())
                .stream()
                .map(savedSalaryGrade -> salaryGradeMapper.toDto(savedSalaryGrade, includeSalaryData))
                .toList();
    }

    @Override
    public SalaryGradeResponse update(
            String id,
            SalaryGradeRequest salaryGradeRequest
    ) throws BadRequestException {

        if (id == null || id.isEmpty()) {
            throw new BadRequestException("SalaryGrade ID must be provided as path");
        }

        final var EXISTING_SALARY_GRADE = salaryGradeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, NotFoundException.EntityType.SALARY_GRADE));

        final var SAVED_SALARY_GRADE = salaryGradeRepository.saveAndFlush(MergeUtil.merge(EXISTING_SALARY_GRADE, salaryGradeMapper.toEntity(salaryGradeRequest)));

        return salaryGradeMapper.toDto(SAVED_SALARY_GRADE, false);
    }

    @Override
    public boolean delete(String id) {
        findById(id, false);
        salaryGradeRepository.deleteById(id);
        return !salaryGradeRepository.existsById(id);
    }

    private void validateSalaryGradeExistence(boolean includeSalaryData, SalaryGradeRequest request) {
        salaryGradeRepository.findBySalaryGradeAndEffectiveDate(
                request.salaryGrade(),
                request.effectiveDate()
        ).ifPresent(salaryGrade -> {
            log.warn("salaryGrade {} and effectiveDate {} already exists in SalaryGrade with id {}",
                    request.salaryGrade(), request.effectiveDate(), salaryGrade.getId());
            throw new IllegalArgumentException(
                    String.format("SalaryGrade with salaryGrade %d and effectiveDate %s already exists.",
                            request.salaryGrade(), request.effectiveDate())
            );
        });

        // Validate salary data if required
        if (includeSalaryData && (request.salaryData() == null || request.salaryData().isEmpty())) {
            throw new IllegalStateException("Salary data must be provided when includeSalaryData is true.");
        }
    }
}