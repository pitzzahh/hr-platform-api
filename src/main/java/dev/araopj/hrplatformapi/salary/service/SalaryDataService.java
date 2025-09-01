package dev.araopj.hrplatformapi.salary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.repository.SalaryDataRepository;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.DiffUtil;
import dev.araopj.hrplatformapi.utils.Mapper;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.enums.CheckType;
import dev.araopj.hrplatformapi.utils.enums.CreateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
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
        var data = salaryDataRepository.findAll().stream()
                .map(entity -> objectMapper.convertValue(entity, SalaryDataResponse.class))
                .toList();
        if (salaryGradeId != null) {
            if (!salaryGradeRepository.existsById(salaryGradeId)) {
                log.warn("SalaryGrade with id {} not found", salaryGradeId);
                throw new NotFoundException(salaryGradeId, NotFoundException.EntityType.SALARY_GRADE);
            }
            data = salaryDataRepository.findBySalaryGrade_Id(
                            salaryGradeId,
                            limit
                    ).stream()
                    .map(entity -> objectMapper.convertValue(entity, SalaryDataResponse.class))
                    .toList();
        }

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

    public Optional<SalaryDataResponse> create(
            SalaryDataRequest salaryDataRequest,
            String salaryGradeId,
            CheckType checkType
    ) throws BadRequestException {
        var optionalSalaryGrade = switch (checkType) {
            case CHECK_PARENT_FROM_REQUEST_PARAM -> salaryGradeRepository.findById(salaryGradeId);
            case CHECK_PARENT_FROM_REQUEST_BODY -> salaryGradeRepository.findById(salaryDataRequest.getSalaryGradeId());
        };

        if (optionalSalaryGrade.isPresent()) {
            var sg = optionalSalaryGrade.get();
            var existing_salary_data = salaryDataRepository.findByStepAndAmountAndSalaryGrade_Id(
                    salaryDataRequest.getStep(),
                    salaryDataRequest.getAmount(),
                    sg.getId()
            );
            if (existing_salary_data.isPresent()) {
                log.warn("SalaryData with step {} and amount {} already exists in SalaryGrade with id {}",
                        salaryDataRequest.getStep(), salaryDataRequest.getAmount(), sg.getId());
                throw new BadRequestException("SalaryData with the same step and amount already exists in the specified SalaryGrade.");
            }
        }

        var data = Mapper.toDto(salaryDataRepository.saveAndFlush(
                SalaryData.builder()
                        .step(salaryDataRequest.getStep())
                        .amount(salaryDataRequest.getAmount())
                        .salaryGrade(optionalSalaryGrade.orElse(null))
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
            CreateType createType,
            boolean useWithParentId
    ) {

        var salaryDataResponse = switch (createType) {
            case FROM_PATH_VARIABLE -> findById(id);
            case FROM_REQUEST_BODY ->
                    findByIdAndSalaryGradeId(id, useWithParentId ? salaryGradeId : salaryDataRequest.getSalaryGradeId());
            default -> Optional.<SalaryDataResponse>empty();
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
