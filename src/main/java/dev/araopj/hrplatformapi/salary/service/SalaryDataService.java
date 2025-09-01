package dev.araopj.hrplatformapi.salary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.repository.SalaryDataRepository;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.*;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.SALARY_DATA;
import static dev.araopj.hrplatformapi.utils.DiffUtil.diff;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;
import static dev.araopj.hrplatformapi.utils.MergeUtil.merge;

@RequiredArgsConstructor
@Service
@Slf4j
public class SalaryDataService {

    private final SalaryGradeRepository salaryGradeRepository;
    private final SalaryDataRepository salaryDataRepository;
    private final ObjectMapper objectMapper;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "salaryGrade");
    private final String ENTITY_NAME = "SalaryDataResponse";

    public List<SalaryDataResponse> findAll(String salaryGradeId, Limit limit) {
        var data = salaryDataRepository.findBySalaryGrade_Id(
                        salaryGradeId,
                        limit
                ).stream()
                .map(entity -> objectMapper.convertValue(entity, SalaryDataResponse.class))
                .toList();
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

    public Optional<SalaryDataResponse> findById(String id) {
        auditUtil.audit(
                id,
                ENTITY_NAME
        );
        return Optional.ofNullable(salaryDataRepository.findById(id)
                .map(e -> objectMapper.convertValue(e, SalaryDataResponse.class))
                .orElseThrow(() -> new NotFoundException(id, SALARY_DATA)));

    }

    public Optional<SalaryDataResponse> findByIdAndSalaryGradeId(String id, String salaryGradeId) {
        var data = salaryDataRepository.findByIdAndSalaryGrade_Id(id, salaryGradeId);

        if (data.isEmpty()) {
            log.warn("{} not found with id {} and salaryGradeId {}", ENTITY_NAME, id, salaryGradeId);
            throw new NotFoundException(id, salaryGradeId, SALARY_DATA, "salaryGradeId");
        }

        auditUtil.audit(
                VIEW,
                id,
                Optional.empty(),
                redact(data.get(), REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );

        return Optional.of(data
                .map(e -> objectMapper.convertValue(e, SalaryDataResponse.class))
                .orElseThrow(() -> new NotFoundException(id, salaryGradeId, SALARY_DATA, "salaryGradeId")));
    }

    public Optional<SalaryDataResponse> create(SalaryDataRequest salaryDataRequest, String salaryGradeId) {
        var salary_grade_id = salaryDataRequest.getSalaryGradeId();

        var optionalSalaryGrade = salaryGradeRepository.findById(salary_grade_id);

        if (optionalSalaryGrade.isEmpty()) {
            log.warn("Checking salary grade with path variable salaryGradeId: {}", salaryGradeId);
            optionalSalaryGrade = salaryGradeRepository.findById(salaryGradeId);
            if (optionalSalaryGrade.isEmpty()) {
                throw new NotFoundException(salaryGradeId, SALARY_DATA);
            }
        }

        var data = Mapper.toDto(salaryDataRepository.saveAndFlush(
                SalaryData.builder()
                        .step(salaryDataRequest.getStep())
                        .amount(salaryDataRequest.getAmount())
                        .salaryGrade(optionalSalaryGrade.get())
                        .build()
        ));
        auditUtil.audit(
                CREATE,
                String.valueOf(salaryDataRequest.getStep()),
                Optional.empty(),
                redact(data, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );
        return Optional.of(data);
    }

    public SalaryDataResponse update(
            String id,
            String salaryGradeId,
            SalaryDataRequest salaryDataRequest,
            FetchType fetchType,
            boolean useParentIdFromPathVariable
    ) {

        var salaryDataResponse = switch (fetchType) {
            case BY_PATH_VARIABLE -> findById(id);
            case WITH_PARENT_PATH_VARIABLE ->
                    findByIdAndSalaryGradeId(id, useParentIdFromPathVariable ? salaryGradeId : salaryDataRequest.getSalaryGradeId());
        };

        if (salaryDataResponse.isEmpty()) {
            log.warn("SalaryData with id {} and salary grade id {} not found", id, salaryGradeId);
            throw new NotFoundException(
                    id,
                    salaryGradeId,
                    SALARY_DATA,
                    "salaryGradeId"
            );
        }

        var oldData = salaryDataResponse.get(); // Keep the old data for auditing
        var newData = MergeUtil.merge(salaryDataResponse.get(), salaryDataRequest); // Merge old salaryDataResponse with new request salaryDataResponse
        var changes = DiffUtil.diff(salaryDataResponse.get(), salaryDataRequest); // Compute the diff between old and new data

        if (changes.isEmpty()) {
            log.info("No changes detected for salary data with id {}", id);
            return oldData;
        }

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(oldData, REDACTED)),
                redact(merge(newData, salaryDataRequest), REDACTED),
                Optional.of(redact(diff(oldData, salaryDataRequest), REDACTED)),
                ENTITY_NAME
        );

        return objectMapper.convertValue(
                salaryDataRepository.saveAndFlush(
                        objectMapper.convertValue(salaryDataResponse, SalaryData.class)
                ),
                SalaryDataResponse.class
        );
    }

    public boolean delete(String id, String salaryGradeId) {
        var data = findByIdAndSalaryGradeId(id, salaryGradeId);
        if (data.isEmpty()) {
            log.warn("Salary data with id {} and salaryGradeId {} not found", id, salaryGradeId);
            return false;
        }
        if (!salaryDataRepository.existsById(id)) {
            log.warn("Salary data with id {} not found for deletion", id);
            return false;
        }
        salaryDataRepository.deleteById(id);
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
