package dev.araopj.hrplatformapi.salary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import dev.araopj.hrplatformapi.salary.repository.SalaryDataRepository;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.Mapper;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.*;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.SALARY_GRADE;
import static dev.araopj.hrplatformapi.utils.DiffUtil.diff;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

/**
 * Service class for managing SalaryGrade entities.
 * Provides methods for retrieving, creating, updating, and deleting salary grades,
 * with optional inclusion of associated salary data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryGradeService {

    private final SalaryGradeRepository salaryGradeRepository;
    private final SalaryDataRepository salaryDataRepository;
    private final ObjectMapper objectMapper;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "salaryData");
    private final String ENTITY_NAME = "SalaryGradeResponse";

    /**
     * Retrieves all salary grades, optionally including associated salary data.
     *
     * @param includeSalaryData whether to include salary data in the response
     * @return list of SalaryGradeResponse objects
     */
    public List<SalaryGradeResponse> findAll(boolean includeSalaryData) {
        List<SalaryGrade> entities;
        if (includeSalaryData) {
            entities = salaryGradeRepository.findAllWithSalaryData();
            log.debug("Fetched {} SalaryGrade records with salary data", entities.size());
        } else {
            entities = salaryGradeRepository.findAll();
            log.debug("Fetched {} SalaryGrade records without salary data", entities.size());
        }

        List<SalaryGradeResponse> data = entities.stream()
                .map(entity -> Mapper.toDto(entity, includeSalaryData))
                .collect(Collectors.toList());

        auditUtil.audit(
                VIEW,
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
     * Retrieves a salary grade by ID, optionally including associated salary data.
     *
     * @param id                the ID of the salary grade
     * @param includeSalaryData whether to include salary data in the response
     * @return Optional containing SalaryGradeResponse if found
     */
    public Optional<SalaryGradeResponse> findById(String id, boolean includeSalaryData) {
        auditUtil.audit(
                id,
                ENTITY_NAME
        );

        final var OPTIONAL_SALARY_GRADE = includeSalaryData ?
                salaryGradeRepository.findSalaryGradeWithSalaryDataById(id) : salaryGradeRepository.findById(id);

        return OPTIONAL_SALARY_GRADE.map(entity -> Mapper.toDto(entity, includeSalaryData));
    }

    /**
     * Creates a new salary grade, optionally with associated salary data.
     *
     * @param salaryGradeRequest the request containing salary grade details
     * @param includeSalaryData  whether to include salary data in creation
     * @return Optional containing the created SalaryGradeResponse
     * @throws BadRequestException if duplicate exists or invalid data
     */
    public Optional<SalaryGradeResponse> create(
            SalaryGradeRequest salaryGradeRequest,
            boolean includeSalaryData
    ) throws BadRequestException {
        var OPTIONAL_SALARY_GRADE = salaryGradeRepository.findBySalaryGradeAndEffectiveDate(
                salaryGradeRequest.salaryGrade(),
                salaryGradeRequest.effectiveDate()
        );

        if (OPTIONAL_SALARY_GRADE.isPresent()) {
            log.warn("salaryGrade {} and effectiveDate {} already exists in SalaryGrade with id {}",
                    salaryGradeRequest.salaryGrade(), salaryGradeRequest.effectiveDate(), OPTIONAL_SALARY_GRADE.get().getId());
            throw new BadRequestException("SalaryGrade with the same salaryGrade and effectiveDate already exists.");
        }

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
                            .map(sd -> SalaryData.builder()
                                    .step(sd.step())
                                    .amount(sd.amount())
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

        return Optional.of(Mapper.toDto(savedSalaryGrade, includeSalaryData));
    }

    /**
     * Updates an existing salary grade.
     *
     * @param id                 the ID of the salary grade to update
     * @param salaryGradeRequest the updated details
     * @return the updated SalaryGradeResponse
     */
    public SalaryGradeResponse update(
            @Nullable String id,
            SalaryGradeRequest salaryGradeRequest
    ) throws BadRequestException {

        if (id == null || id.isEmpty()) {
            throw new BadRequestException("SalaryGrade ID must be provided as path");
        }

        final var SALARY_GRADE = MergeUtil.merge(salaryGradeRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(id, NotFoundException.EntityType.SALARY_GRADE)),
                Mapper.toEntity(salaryGradeRequest)
        );

        final var OLD_REDACTED = redact(SALARY_GRADE, REDACTED);

        final var SAVED_SALARY_GRADE = salaryGradeRepository.saveAndFlush(SALARY_GRADE);

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(OLD_REDACTED),
                redact(SAVED_SALARY_GRADE, REDACTED),
                Optional.of(redact(diff(Mapper.toDto(SALARY_GRADE, false), salaryGradeRequest), REDACTED)), // Adjust diff if needed
                ENTITY_NAME
        );

        return Mapper.toDto(SAVED_SALARY_GRADE, false); // false as update doesn't include data yet
    }

    /**
     * Deletes a salary grade by ID.
     *
     * @param id the ID to delete
     * @return true if deleted
     */
    public boolean delete(String id) {
        SalaryGrade entity = salaryGradeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, SALARY_GRADE));
        salaryGradeRepository.deleteById(id);
        auditUtil.audit(
                DELETE,
                id,
                Optional.of(redact(entity, REDACTED)),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );
        return true;
    }

}