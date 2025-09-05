package dev.araopj.hrplatformapi.salary.service.impl;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.salary.service.SalaryGradeService;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.Mapper;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.*;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.SALARY_GRADE;
import static dev.araopj.hrplatformapi.utils.DiffUtil.diff;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryGradeServiceImp implements SalaryGradeService {

    private final SalaryGradeRepository salaryGradeRepository;
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
                .map(entity -> Mapper.toDto(entity, includeSalaryData));
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

        return OPTIONAL_SALARY_GRADE.map(entity -> Mapper.toDto(entity, includeSalaryData));
    }

    @Override
    public SalaryGradeResponse create(
            SalaryGradeRequest salaryGradeRequest,
            boolean includeSalaryData
    ) throws BadRequestException {
        validateSalaryGradeExistence(includeSalaryData, salaryGradeRequest);

        var salaryGradeToSave = SalaryGrade.builder()
                .legalBasis(salaryGradeRequest.legalBasis())
                .tranche(salaryGradeRequest.tranche())
                .effectiveDate(salaryGradeRequest.effectiveDate())
                .salaryGrade(salaryGradeRequest.salaryGrade())
                .build();

        if (includeSalaryData) {
            if (salaryGradeRequest.salaryData() == null || salaryGradeRequest.salaryData().isEmpty()) {
                throw new BadRequestException("Salary data must be provided when includeSalaryData is true.");
            }
            salaryGradeToSave.setSalaryData(
                    salaryGradeRequest.salaryData().stream()
                            .map(salaryDataRequest -> SalaryData.builder()
                                    .step(salaryDataRequest.step())
                                    .amount(salaryDataRequest.amount())
                                    .salaryGrade(salaryGradeToSave)
                                    .build())
                            .collect(Collectors.toList())
            );
        }

        var savedSalaryGrade = salaryGradeRepository.save(salaryGradeToSave);
        log.debug("Saved salary grade: {}", savedSalaryGrade);

        auditUtil.audit(
                CREATE,
                savedSalaryGrade.getId(),
                Optional.empty(),
                redact(savedSalaryGrade, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );

        return Mapper.toDto(savedSalaryGrade, includeSalaryData);
    }

    @Override
    public List<SalaryGradeResponse> createBatch(
            List<SalaryGradeRequest> salaryGradeRequests,
            boolean includeSalaryData
    ) throws BadRequestException {
        var salaryGradesToSave = new ArrayList<SalaryGrade>();
        var responses = new ArrayList<SalaryGradeResponse>();

        // Validate all requests first
        for (var request : salaryGradeRequests) {
            validateSalaryGradeExistence(includeSalaryData, request);
        }

        // Build salary grade entities
        for (var request : salaryGradeRequests) {
            var salaryGradeToSave = SalaryGrade.builder()
                    .legalBasis(request.legalBasis())
                    .tranche(request.tranche())
                    .effectiveDate(request.effectiveDate())
                    .salaryGrade(request.salaryGrade())
                    .build();

            if (includeSalaryData) {
                salaryGradeToSave.setSalaryData(
                        request.salaryData().stream()
                                .map(salaryDataRequest -> SalaryData.builder()
                                        .step(salaryDataRequest.step())
                                        .amount(salaryDataRequest.amount())
                                        .salaryGrade(salaryGradeToSave)
                                        .build())
                                .collect(Collectors.toList())
                );
            }

            salaryGradesToSave.add(salaryGradeToSave);
        }

        // Save all salary grades in a single batch
        var savedSalaryGrades = salaryGradeRepository.saveAll(salaryGradesToSave);
        log.debug("Saved {} salary grades in batch", savedSalaryGrades.size());

        // Audit each saved salary grade
        for (var savedSalaryGrade : savedSalaryGrades) {
            auditUtil.audit(
                    CREATE,
                    savedSalaryGrade.getId(),
                    Optional.empty(),
                    redact(savedSalaryGrade, REDACTED),
                    Optional.empty(),
                    ENTITY_NAME
            );
            responses.add(Mapper.toDto(savedSalaryGrade, includeSalaryData));
        }

        return responses;
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
        final var OLD_DTO = Mapper.toDto(EXISTING_SALARY_GRADE, false);
        final var SALARY_GRADE = MergeUtil.merge(EXISTING_SALARY_GRADE, Mapper.toEntity(salaryGradeRequest));

        final var OLD_REDACTED = redact(EXISTING_SALARY_GRADE, REDACTED);

        final var SAVED_SALARY_GRADE = salaryGradeRepository.saveAndFlush(SALARY_GRADE);
        final var NEW_DTO = Mapper.toDto(SAVED_SALARY_GRADE, false);

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
                Optional.of(redact(Mapper.toDto(SALARY_GRADE_TO_BE_REMOVED, false), REDACTED)), // Redact DTO instead of entity
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );

        salaryGradeRepository.deleteById(id);
        return true;
    }

    private void validateSalaryGradeExistence(boolean includeSalaryData, SalaryGradeRequest request) throws BadRequestException {
        final var OPTIONAL_SALARY_GRADE = salaryGradeRepository.findBySalaryGradeAndEffectiveDate(
                request.salaryGrade(),
                request.effectiveDate()
        );

        if (OPTIONAL_SALARY_GRADE.isPresent()) {
            log.warn("salaryGrade {} and effectiveDate {} already exists in SalaryGrade with id {}",
                    request.salaryGrade(), request.effectiveDate(), OPTIONAL_SALARY_GRADE.get().getId());
            throw new BadRequestException(
                    String.format("SalaryGrade with salaryGrade %d and effectiveDate %s already exists.",
                            request.salaryGrade(), request.effectiveDate())
            );
        }

        // Validate salary data if required
        if (includeSalaryData && request.salaryData() == null || request.salaryData().isEmpty()) {
            throw new BadRequestException("Salary data must be provided when includeSalaryData is true.");
        }
    }
}