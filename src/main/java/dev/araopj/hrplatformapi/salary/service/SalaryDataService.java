package dev.araopj.hrplatformapi.salary.service;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.repository.SalaryDataRepository;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.Mapper;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.enums.CheckType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.*;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.SALARY_DATA;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.SALARY_GRADE;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

@RequiredArgsConstructor
@Service
@Slf4j
public class SalaryDataService {

    private final SalaryGradeRepository salaryGradeRepository;
    private final SalaryDataRepository salaryDataRepository;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "salaryGrade");
    private final String ENTITY_NAME = "SalaryDataResponse";

    public List<SalaryDataResponse> findAll(String salaryGradeId, boolean withSalaryGrade, int limit) {

        var pageable = PageRequest.of(0, limit);

        var data = withSalaryGrade ?
                salaryDataRepository.findAllWithParent(pageable)
                : salaryDataRepository.findAll(pageable).getContent();

        if (salaryGradeId != null) {
            if (!salaryGradeRepository.existsById(salaryGradeId)) {
                log.warn("SalaryGrade with id {} not found", salaryGradeId);
                throw new NotFoundException(salaryGradeId, NotFoundException.EntityType.SALARY_GRADE);
            }
            data = salaryDataRepository.findBySalaryGradeId(salaryGradeId, pageable);
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

        return data.stream()
                .map(entity -> Mapper.toDto(entity, withSalaryGrade))
                .toList();
    }

    public Optional<SalaryDataResponse> findById(String id) {
        auditUtil.audit(
                id,
                ENTITY_NAME
        );
        return Optional.ofNullable(salaryDataRepository.findById(id)
                .map(Mapper::toDto)
                .orElseThrow(() -> new NotFoundException(id, SALARY_DATA)));

    }

    public Optional<SalaryDataResponse> findByIdAndSalaryGradeId(String id, String salaryGradeId) {
        var data = salaryDataRepository.findByIdAndSalaryGradeId(id, salaryGradeId);

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
                .map(Mapper::toDto)
                .orElseThrow(() -> new NotFoundException(id, salaryGradeId, SALARY_DATA, "salaryGradeId")));
    }

    public Optional<SalaryDataResponse> create(
            SalaryDataRequest salaryDataRequest,
            String salaryGradeId, // salary grade id from request param
            CheckType checkType
    ) throws BadRequestException {
        final var OPTIONAL_SALARY_GRADE_CHECK = switch (checkType) {
            case CHECK_PARENT_FROM_REQUEST_PARAM -> {
                if (salaryGradeId.isEmpty()) {
                    throw new BadRequestException("Salary grade ID must be provided as query parameter when using CHECK_PARENT_FROM_REQUEST_PARAM.");
                }
                yield salaryGradeRepository.findById(salaryGradeId);
            }
            case CHECK_PARENT_FROM_REQUEST_BODY -> {
                if (salaryDataRequest.salaryGradeId().isEmpty()) {
                    throw new BadRequestException("Salary grade ID must be provided in request body when using CHECK_PARENT_FROM_REQUEST_BODY.");
                }
                yield salaryGradeRepository.findById(salaryDataRequest.salaryGradeId());
            }
        };


        final var SALARY_GRADE_ID_TO_CHECK = getId(salaryDataRequest, salaryGradeId, checkType);
        final var RESOLVED_SALARY_GRADE = OPTIONAL_SALARY_GRADE_CHECK.orElseThrow(() -> new NotFoundException(SALARY_GRADE_ID_TO_CHECK, SALARY_GRADE));

        salaryDataRepository.findByStepAndAmountAndSalaryGrade_Id(salaryDataRequest.step(), salaryDataRequest.amount(), SALARY_GRADE_ID_TO_CHECK)
                .ifPresent(sd -> {
                    throw new IllegalArgumentException(
                            "Salary data with step %d amount %f, and salary grade %d already exists for salary grade ID [%s]".formatted(
                                    salaryDataRequest.step(),
                                    salaryDataRequest.amount(),
                                    RESOLVED_SALARY_GRADE.getSalaryGrade(),
                                    SALARY_GRADE_ID_TO_CHECK
                            )
                    );
                });


        final var SALARY_DATA_TO_SAVE = SalaryData.builder()
                .step(salaryDataRequest.step())
                .amount(salaryDataRequest.amount())
                .salaryGrade(RESOLVED_SALARY_GRADE)
                .build();

        auditUtil.audit(
                CREATE,
                String.valueOf(SALARY_DATA_TO_SAVE.getId()),
                Optional.empty(),
                redact(SALARY_DATA_TO_SAVE, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );
        return Optional.of(Mapper.toDto(salaryDataRepository.saveAndFlush(SALARY_DATA_TO_SAVE)));

    }

    public SalaryDataResponse update(
            String id,
            SalaryDataRequest salaryDataRequest
    ) throws BadRequestException {

        if (id == null || id.isEmpty()) {
            throw new BadRequestException("SalaryData ID must be provided as path");
        }

        final var SALARY_GRADE_ID = salaryDataRequest.salaryGradeId();

        var SALARY_DATA = MergeUtil.merge(salaryDataRepository
                        .findById(id).orElseThrow(() -> new NotFoundException(id, NotFoundException.EntityType.SALARY_DATA)),
                Mapper.toEntity(salaryDataRequest)
        );

        if (SALARY_GRADE_ID != null && !SALARY_GRADE_ID.isEmpty()) {
            final var SALARY_GRADE_PARENT = findByIdAndSalaryGradeId(id, SALARY_GRADE_ID)
                    .orElseThrow(() -> new NotFoundException(id, SALARY_GRADE_ID, NotFoundException.EntityType.SALARY_DATA, "salaryGradeId"));
            if (!Objects.equals(SALARY_GRADE_PARENT.id(), SALARY_DATA.getSalaryGrade().getId())) {
                throw new BadRequestException("SalaryData with id %s does not belong to SalaryGrade with id %s".formatted(id, SALARY_GRADE_ID));
            }

            // check for existing entity based on new update values
            salaryDataRepository.findByStepAndAmountAndSalaryGrade_Id(
                    salaryDataRequest.step(),
                    salaryDataRequest.amount(),
                    SALARY_GRADE_ID
            ).ifPresent(e -> {
                if (!Objects.equals(e.getId(), id)) {
                    throw new IllegalArgumentException(
                            "Salary data with step %d amount %s, and salary grade %d already exists for salary grade ID [%s]".formatted(
                                    salaryDataRequest.step(),
                                    salaryDataRequest.amount() % 1 == 0 ? String.format("%.0f", salaryDataRequest.amount()) : String.format("%.2f", salaryDataRequest.amount()),
                                    e.getSalaryGrade().getSalaryGrade(),
                                    SALARY_GRADE_ID
                            )
                    );
                }
            });
        }

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(SALARY_DATA, REDACTED)),
                redact(MergeUtil.merge(SALARY_DATA, SALARY_DATA), REDACTED),
                Optional.of(redact(SALARY_DATA, REDACTED)),
                ENTITY_NAME
        );

        return Mapper.toDto(salaryDataRepository.saveAndFlush(SALARY_DATA));
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

    private static String getId(SalaryDataRequest request, String salaryGradeId, CheckType checkType) throws
            BadRequestException {
        switch (checkType) {
            case CHECK_PARENT_FROM_REQUEST_BODY -> {
                if (salaryGradeId == null || salaryGradeId.isEmpty())
                    throw new BadRequestException("Salary grade ID must be provided in request body when using CHECK_PARENT_FROM_REQUEST_BODY.");
                return request.salaryGradeId();
            }
            case CHECK_PARENT_FROM_REQUEST_PARAM -> {
                if (salaryGradeId == null || salaryGradeId.isEmpty())
                    throw new BadRequestException("Salary grade ID must be provided as query parameter when using CHECK_PARENT_FROM_REQUEST_PARAM.");
                return salaryGradeId;
            }
            default -> throw new BadRequestException("Invalid CheckType provided.");
        }
    }
}
