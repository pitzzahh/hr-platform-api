package dev.araopj.hrplatformapi.salary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import dev.araopj.hrplatformapi.salary.repository.SalaryDataRepository;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.DiffUtil;
import dev.araopj.hrplatformapi.utils.MergeUtil;
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
import static dev.araopj.hrplatformapi.utils.MergeUtil.merge;

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

    public List<SalaryGradeResponse> findAll(boolean includeSalaryData) {
        var data = salaryGradeRepository.findAll().stream()
                .map(entity -> SalaryGradeResponse.builder()
                        .id(entity.getId())
                        .legalBasis(entity.getLegalBasis())
                        .tranche(entity.getTranche())
                        .effectiveDate(entity.getEffectiveDate())
                        .salaryGrade(entity.getSalaryGrade())
                        .createdAt(entity.getCreatedAt())
                        .updatedAt(entity.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        log.debug("Fetched {} SalaryGrade records without salary data", data);
//        if (includeSalaryData) {
//            data = salaryGradeRepository.findAllWithSalaryData().stream()
//                    .map(entity -> SalaryGradeResponse.builder()
//                            .id(entity.getId())
//                            .legalBasis(entity.getLegalBasis())
//                            .tranche(entity.getTranche())
//                            .effectiveDate(entity.getEffectiveDate())
//                            .salaryGrade(entity.getSalaryGrade())
//                            .salaryData(
//                                    entity.getSalaryData()
//                                            .stream()
//                                            .map(e -> SalaryDataResponse
//                                                    .builder()
//                                                    .step(e.getStep())
//                                                    .amount(e.getAmount())
//                                                    .createdAt(e.getCreatedAt())
//                                                    .updatedAt(e.getUpdatedAt())
//                                                    .build())
//                                            .collect(Collectors.toSet())
//                            )
//                            .build())
//                    .collect(Collectors.toList());
//        }

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

    public Optional<SalaryGradeResponse> findById(String id, boolean includeSalaryData) {
        auditUtil.audit(
                id,
                ENTITY_NAME
        );

        return salaryGradeRepository.findById(id)
                .map(e -> objectMapper.convertValue(e, SalaryGradeResponse.class));

//        return Optional.ofNullable(Optional.ofNullable(
//                        includeSalaryData ?
//                                salaryGradeRepository.findSalaryGradeWithSalaryDataById(id) :
//                                salaryGradeRepository.findById(id)
//                ).map(e -> objectMapper.convertValue(e, SalaryGradeResponse.class))
//                .orElseThrow(() -> new NotFoundException(id, SALARY_GRADE)));

    }

    public Optional<SalaryGradeResponse> create(
            SalaryGradeRequest salaryGradeRequest,
            boolean includeSalaryData
    ) throws BadRequestException {
        var optionalSalaryGrade = salaryGradeRepository.findBySalaryGradeAndEffectiveDate(
                salaryGradeRequest.salaryGrade(),
                salaryGradeRequest.effectiveDate()
        );

        if (optionalSalaryGrade.isPresent()) {
            log.warn("salaryGrade {} and effectiveDate {} already exists in SalaryGrade with id {}",
                    salaryGradeRequest.salaryGrade(), salaryGradeRequest.effectiveDate(), optionalSalaryGrade.get().getId());
            throw new BadRequestException("SalaryGrade with the same salaryGrade and effectiveDate already exists in the specified SalaryGrade.");
        }

        var savedSalaryGrade = salaryGradeRepository.save(SalaryGrade.builder()
                        .legalBasis(salaryGradeRequest.legalBasis())
                        .tranche(salaryGradeRequest.tranche())
                        .effectiveDate(salaryGradeRequest.effectiveDate())
                        .salaryGrade(salaryGradeRequest.salaryGrade())
//                .salaryData(includeSalaryData ? salaryGradeRequest.salaryData()
//                        .stream()
//                        .map(e -> SalaryData.builder()
//                                .step(e.step())
//                                .amount(e.amount())
//                                .build())
//                        .collect(Collectors.toSet()) : Set.of())
                        .build()
        );

        log.debug("Saved salary grade: {}", savedSalaryGrade);

        var salaryGradeResponse = SalaryGradeResponse.builder()
                .id(savedSalaryGrade.getId())
                .legalBasis(savedSalaryGrade.getLegalBasis())
                .tranche(savedSalaryGrade.getTranche())
                .effectiveDate(savedSalaryGrade.getEffectiveDate())
                .salaryGrade(savedSalaryGrade.getSalaryGrade())
                .createdAt(savedSalaryGrade.getCreatedAt())
//                .salaryData(
//                        includeSalaryData ?
//                                savedSalaryGrade.getSalaryData()
//                                        .stream()
//                                        .map(e -> SalaryDataResponse.builder()
//                                                .id(e.getId())
//                                                .step(e.getStep())
//                                                .amount(e.getAmount())
//                                                .createdAt(e.getCreatedAt())
//                                                .updatedAt(e.getUpdatedAt())
//                                                .build())
//                                        .collect(Collectors.toSet()) : Set.of()
//                )
                .updatedAt(savedSalaryGrade.getUpdatedAt())
                .build();

//        if (includeSalaryData) {
//            var salaryDataToSave = salaryGradeRequest.salaryData()
//                    .stream()
//                    .map(e -> SalaryData.builder()
//                            .step(e.step())
//                            .amount(e.amount())
//                            .salaryGrade(savedSalaryGrade)
//                            .build())
//                    .collect(Collectors.toSet());
//            log.debug("Saving salary data: {}", salaryDataToSave);
//
//            var salaryData = salaryDataRepository.saveAll(salaryDataToSave)
//                    .stream()
//                    .map(e -> SalaryDataResponse.builder()
//                            .id(e.getId())
//                            .step(e.getStep())
//                            .amount(e.getAmount())
//                            .createdAt(e.getCreatedAt())
//                            .updatedAt(e.getUpdatedAt())
//                            .build())
//                    .collect(Collectors.toSet());
//
//            salaryGradeResponse.salaryData(salaryData);
//        }

        log.debug("Salary grade response: {}", salaryGradeResponse);

        auditUtil.audit(
                CREATE,
                salaryGradeResponse.id(),
                Optional.empty(),
                redact(savedSalaryGrade, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );
        return Optional.of(salaryGradeResponse);
    }

    public SalaryGradeResponse update(
            String id,
            SalaryGradeRequest salaryGradeRequest
    ) {
        var optionalSalaryGrade = findById(id, false);

        if (optionalSalaryGrade.isEmpty()) {
            log.warn("SalaryGrade with id {} not found", id);
            throw new NotFoundException(
                    id,
                    SALARY_GRADE
            );
        }

        var oldData = optionalSalaryGrade.get();
        var newData = MergeUtil.merge(optionalSalaryGrade.get(), salaryGradeRequest);
        var changes = DiffUtil.diff(optionalSalaryGrade.get(), salaryGradeRequest);

        if (changes.isEmpty()) {
            log.info("No changes detected for SalaryGrade with id {}", id);
            return oldData;
        }

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(oldData, REDACTED)),
                redact(merge(newData, salaryGradeRequest), REDACTED),
                Optional.of(redact(diff(oldData, salaryGradeRequest), REDACTED)),
                ENTITY_NAME
        );

        return objectMapper.convertValue(
                salaryGradeRepository.saveAndFlush(
                        objectMapper.convertValue(changes, SalaryGrade.class)
                ),
                SalaryGradeResponse.class
        );
    }

    public boolean delete(String id) {
        findById(id, false)
                .orElseThrow(() -> new NotFoundException(id, SALARY_GRADE));
        salaryGradeRepository.deleteById(id);
        auditUtil.audit(
                DELETE,
                id,
                Optional.of(id),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );
        return true;
    }
}
