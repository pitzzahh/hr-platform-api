package dev.araopj.hrplatformapi.salary.service;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.repository.SalaryDataRepository;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.utils.*;
import dev.araopj.hrplatformapi.utils.enums.CheckType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.*;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.SALARY_DATA;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.SALARY_GRADE;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

@RequiredArgsConstructor
@Service
@Slf4j
public class SalaryDataServiceImp implements ISalaryDataService {

    private final SalaryGradeRepository salaryGradeRepository;
    private final SalaryDataRepository salaryDataRepository;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "salaryGrade");
    private final String ENTITY_NAME = "SalaryDataResponse";

    @Override
    public Page<SalaryDataResponse> findAll(Pageable pageable) {
        final var SALARY_DATA = salaryDataRepository.findAll(pageable);

        auditUtil.audit(
                VIEW,
                "[]",
                Optional.empty(),
                PaginationMeta.builder()
                        .totalElements(SALARY_DATA.getTotalElements())
                        .size(SALARY_DATA.getSize())
                        .page(SALARY_DATA.getNumber() + 1)
                        .totalPages(SALARY_DATA.getTotalPages())
                        .build(),
                Optional.empty(),
                ENTITY_NAME
        );

        return SALARY_DATA
                .map(Mapper::toDto);
    }

    @Override
    public Optional<SalaryDataResponse> findById(String id) {
        auditUtil.audit(
                id,
                ENTITY_NAME
        );
        return Optional.of(salaryDataRepository.findById(id)
                .map(Mapper::toDto)
                .orElseThrow(() -> new NotFoundException(id, SALARY_DATA)));

    }

    @Override
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

    @Override
    public SalaryDataResponse create(
            SalaryDataRequest salaryDataRequest
    ) {

        final var SALARY_GRADE_ID = salaryDataRequest.salaryGradeId();
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

        final var SALARY_DATA_TO_SAVE = SalaryData.builder()
                .step(salaryDataRequest.step())
                .amount(salaryDataRequest.amount())
                .salaryGrade(salaryGradeRepository
                        .findById(SALARY_GRADE_ID)
                        .orElseThrow(() -> new NotFoundException(SALARY_GRADE_ID, SALARY_GRADE))
                ).build();

        auditUtil.audit(
                CREATE,
                SALARY_DATA_TO_SAVE.getId(),
                Optional.empty(),
                redact(SALARY_DATA_TO_SAVE, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );
        return Mapper.toDto(salaryDataRepository.save(SALARY_DATA_TO_SAVE));

    }

    @Override
    public SalaryDataResponse update(
            String id,
            SalaryDataRequest.WithoutSalaryGradeId salaryDataRequest
    ) throws BadRequestException {

        if (id == null || id.isEmpty()) {
            throw new BadRequestException("SalaryData ID must be provided as path");
        }

        final var ORIGINAL_SALARY_DATA = salaryDataRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, SALARY_DATA));
        var SALARY_DATA = MergeUtil.merge(ORIGINAL_SALARY_DATA,
                Mapper.toEntity(salaryDataRequest)
        );

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(ORIGINAL_SALARY_DATA, REDACTED)),
                redact(SALARY_DATA, REDACTED),
                Optional.of(redact(DiffUtil.diff(ORIGINAL_SALARY_DATA, SALARY_DATA), REDACTED)),
                ENTITY_NAME
        );

        return Mapper.toDto(salaryDataRepository.save(SALARY_DATA));
    }

    @Override
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

    private static String getId(SalaryDataRequest salaryDataRequest, String salaryGradeId, CheckType checkType) throws
            BadRequestException {
        switch (checkType) {
            case CHECK_PARENT_FROM_REQUEST_PARAM -> {
                if (salaryGradeId == null || salaryGradeId.isEmpty())
                    throw new BadRequestException("Salary grade ID must be provided as query parameter when using CHECK_PARENT_FROM_REQUEST_PARAM.");
                return salaryGradeId;
            }
            case CHECK_PARENT_FROM_REQUEST_BODY -> {
                if (salaryDataRequest.salaryGradeId() == null || salaryDataRequest.salaryGradeId().isEmpty())
                    throw new BadRequestException("Salary grade ID must be provided in salaryDataRequest body when using CHECK_PARENT_FROM_REQUEST_BODY.");
                return salaryDataRequest.salaryGradeId();
            }
            default -> throw new BadRequestException("Invalid CheckType provided.");
        }
    }
}
