package dev.araopj.hrplatformapi.salary.service.impl;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.repository.SalaryDataRepository;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.salary.service.SalaryDataService;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.SalaryDataMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.SALARY_DATA;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.SALARY_GRADE;

@RequiredArgsConstructor
@Service
@Slf4j
public class SalaryDataServiceImp implements SalaryDataService {

    private final SalaryGradeRepository salaryGradeRepository;
    private final SalaryDataRepository salaryDataRepository;
    private final SalaryDataMapper salaryDataMapper;

    @Override
    public Page<SalaryDataResponse> findAll(Pageable pageable) {
        return salaryDataRepository.findAll(pageable).map(e -> salaryDataMapper.toDto(e, e.getSalaryGrade()));
    }

    @Override
    public Optional<SalaryDataResponse> findById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        return Optional.of(salaryDataRepository.findById(id)
                .map(e -> salaryDataMapper.toDto(e, e.getSalaryGrade()))
                .orElseThrow(() -> new NotFoundException(id, SALARY_DATA)));

    }

    @Override
    public Optional<SalaryDataResponse> findByIdAndSalaryGradeId(String id, String salaryGradeId) {
        var salaryData = salaryDataRepository.findByIdAndSalaryGradeId(id, salaryGradeId)
                .orElseThrow(() -> new NotFoundException(id, salaryGradeId, SALARY_DATA, "salaryGradeId"));

        return Optional.of(salaryDataMapper.toDto(salaryData, salaryData.getSalaryGrade()));
    }

    @Override
    public SalaryDataResponse create(
            SalaryDataRequest salaryDataRequest
    ) {

        if (salaryDataRequest == null) {
            throw new IllegalArgumentException("salaryDataRequest cannot be null");
        }

        final var SALARY_GRADE_ID = salaryDataRequest.salaryGradeId();

        if (SALARY_GRADE_ID == null ||
                (SALARY_GRADE_ID.trim().isEmpty())) {
            throw new IllegalArgumentException("Salary grade ID cannot be null or blank");
        }
        salaryDataRepository.findByStepAndAmountAndSalaryGradeId(salaryDataRequest.step(), salaryDataRequest.amount(), SALARY_GRADE_ID)
                .ifPresent(sd -> {
                    throw new IllegalArgumentException(
                            "Salary data with step %d amount %f, and salary grade %d already exists for salary grade ID [%s]".formatted(
                                    salaryDataRequest.step(),
                                    salaryDataRequest.amount(),
                                    sd.getSalaryGrade().getSalaryGrade(),
                                    SALARY_GRADE_ID
                            )
                    );
                });

        final var SALARY_DATA_TO_SAVE = salaryDataMapper.toEntity(salaryDataRequest,
                salaryGradeRepository.findById(SALARY_GRADE_ID)
                        .orElseThrow(() -> new NotFoundException(SALARY_GRADE_ID, SALARY_GRADE))
        );
        return salaryDataMapper.toDto(salaryDataRepository.save(SALARY_DATA_TO_SAVE), SALARY_DATA_TO_SAVE.getSalaryGrade());
    }

    @Override
    public SalaryDataResponse update(
            String id,
            @Valid SalaryDataRequest.WithoutSalaryGradeId salaryDataRequest
    ) throws IllegalArgumentException {

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("SalaryData ID must be provided as path");
        }

        if (salaryDataRequest == null) {
            throw new IllegalArgumentException("salaryDataRequest cannot be null");
        }

        final var ORIGINAL_SALARY_DATA = salaryDataRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, SALARY_DATA));

        var SALARY_DATA = MergeUtil.merge(ORIGINAL_SALARY_DATA,
                salaryDataMapper.toEntity(salaryDataRequest)
        );
        return salaryDataMapper.toDto(salaryDataRepository.save(SALARY_DATA), SALARY_DATA.getSalaryGrade());
    }

    @Override
    public boolean delete(String id, String salaryGradeId) {
        findByIdAndSalaryGradeId(id, salaryGradeId).orElseThrow();
        salaryDataRepository.deleteById(id);
        return !salaryDataRepository.existsById(id);
    }
}
