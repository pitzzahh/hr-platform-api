package dev.araopj.hrplatformapi.salary.service.impl;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.salary.service.SalaryGradeService;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.SalaryDataMapper;
import dev.araopj.hrplatformapi.utils.mappers.SalaryGradeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.*;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.SALARY_GRADE;
import static dev.araopj.hrplatformapi.utils.DiffUtil.diff;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryGradeServiceImp implements SalaryGradeService {

    private final SalaryGradeRepository salaryGradeRepository;
    private final SalaryGradeMapper salaryGradeMapper;
    private final SalaryDataMapper salaryDataMapper;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "salaryData");
    private final String ENTITY_NAME = SalaryGradeResponse.class.getName();

    @Override
    public Page<SalaryGradeResponse> findAll(Pageable pageable, boolean includeSalaryData) {
        final var SALARY_GRADES = includeSalaryData ?
                salaryGradeRepository.findAllWithSalaryData(pageable) : salaryGradeRepository.findAll(pageable);
        auditUtil.audit(
                VIEW,
                "[]",
                Optional.of(Map.of(
                        "timestamp", Instant.now().toString(),
                        "entity", ENTITY_NAME,
                        "count", SALARY_GRADES
                )),
                Optional.empty(),
                Optional.empty(),
                "List<%s>".formatted(ENTITY_NAME)
        );
        return SALARY_GRADES
                .map(entity -> salaryGradeMapper.toDto(entity, includeSalaryData));
    }

    @Override
    public Optional<SalaryGradeResponse> findById(String id, boolean includeSalaryData) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("SalaryGrade ID must be provided as path");
        }

        auditUtil.audit(
                id,
                ENTITY_NAME
        );

        final var OPTIONAL_SALARY_GRADE = includeSalaryData ?
                salaryGradeRepository.findSalaryGradeWithSalaryDataById(id) : salaryGradeRepository.findById(id);

        return OPTIONAL_SALARY_GRADE.map(entity -> salaryGradeMapper.toDto(entity, includeSalaryData));
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

        var salaryGradesToSave = salaryGradeRequests.stream()
                .map(request -> {
                    var salaryGrade = salaryGradeMapper.toEntity(request);

                    if (includeSalaryData && request.salaryData() != null) {
                        salaryGrade.setSalaryData(
                                request.salaryData().stream()
                                        .map(salaryDataMapper::toEntity)
                                        .toList()
                        );
                    }
                    return salaryGrade;
                })
                .toList();

        var savedSalaryGrades = salaryGradeRepository.saveAll(salaryGradesToSave);

        log.debug("Saved {} salary grades in batch", savedSalaryGrades.size());

        return savedSalaryGrades.stream()
                .map(savedSalaryGrade -> {
                    auditUtil.audit(
                            CREATE,
                            savedSalaryGrade.getId(),
                            Optional.empty(),
                            redact(savedSalaryGrade, REDACTED),
                            Optional.empty(),
                            ENTITY_NAME
                    );
                    return salaryGradeMapper.toDto(savedSalaryGrade, includeSalaryData);
                })
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
        final var OLD_DTO = salaryGradeMapper.toDto(EXISTING_SALARY_GRADE, false);
        final var SALARY_GRADE = MergeUtil.merge(EXISTING_SALARY_GRADE, salaryGradeMapper.toEntity(salaryGradeRequest));

        final var OLD_REDACTED = redact(EXISTING_SALARY_GRADE, REDACTED);

        final var SAVED_SALARY_GRADE = salaryGradeRepository.saveAndFlush(SALARY_GRADE);
        final var NEW_DTO = salaryGradeMapper.toDto(SAVED_SALARY_GRADE, false);

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(OLD_REDACTED),
                redact(SAVED_SALARY_GRADE, REDACTED),
                Optional.of(redact(diff(OLD_DTO, NEW_DTO), REDACTED)),
                ENTITY_NAME
        );

        return NEW_DTO;
    }

    @Override
    public boolean delete(String id) {
        var SALARY_GRADE_TO_BE_REMOVED = salaryGradeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, SALARY_GRADE));

        auditUtil.audit(
                DELETE,
                id,
                Optional.of(redact(salaryGradeMapper.toDto(SALARY_GRADE_TO_BE_REMOVED, false), REDACTED)),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );

        salaryGradeRepository.deleteById(id);
        return true;
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