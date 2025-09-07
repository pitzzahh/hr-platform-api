package dev.araopj.hrplatformapi.salary.service.impl;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.repository.SalaryDataRepository;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.salary.service.SalaryDataService;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.DiffUtil;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.PaginationMeta;
import dev.araopj.hrplatformapi.utils.mappers.SalaryDataMapper;
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
public class SalaryDataServiceImp implements SalaryDataService {

    private final SalaryGradeRepository salaryGradeRepository;
    private final SalaryDataRepository salaryDataRepository;
    private final SalaryDataMapper salaryDataMapper;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "salaryGrade");
    private final String ENTITY_NAME = SalaryDataResponse.class.getName();

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
                .map(e -> salaryDataMapper.toDto(e, e.getSalaryGrade()));
    }

    @Override
    public Optional<SalaryDataResponse> findById(String id) {
        auditUtil.audit(
                id,
                ENTITY_NAME
        );
        return Optional.of(salaryDataRepository.findById(id)
                .map(e -> salaryDataMapper.toDto(e, e.getSalaryGrade()))
                .orElseThrow(() -> new NotFoundException(id, SALARY_DATA)));

    }

    @Override
    public Optional<SalaryDataResponse> findByIdAndSalaryGradeId(String id, String salaryGradeId) {
        var optionalSalaryData = salaryDataRepository.findByIdAndSalaryGradeId(id, salaryGradeId);
        if (optionalSalaryData.isEmpty()) {
            log.warn("{} not found with id {} and salaryGradeId {}", ENTITY_NAME, id, salaryGradeId);
            throw new NotFoundException(id, salaryGradeId, SALARY_DATA, "salaryGradeId");
        }

        auditUtil.audit(
                VIEW,
                id,
                Optional.empty(),
                redact(optionalSalaryData.get(), REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );

        return Optional.of(optionalSalaryData
                .map(e -> salaryDataMapper.toDto(e, e.getSalaryGrade()))
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

        final var SALARY_DATA_TO_SAVE = salaryDataMapper.toEntity(salaryDataRequest,
                salaryGradeRepository.findById(SALARY_GRADE_ID)
                        .orElseThrow(() -> new NotFoundException(SALARY_GRADE_ID, SALARY_GRADE))
        );

        auditUtil.audit(
                CREATE,
                SALARY_DATA_TO_SAVE.getId(),
                Optional.empty(),
                redact(SALARY_DATA_TO_SAVE, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );
        return salaryDataMapper.toDto(salaryDataRepository.save(SALARY_DATA_TO_SAVE), SALARY_DATA_TO_SAVE.getSalaryGrade());
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
                salaryDataMapper.toEntity(salaryDataRequest)
        );

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(ORIGINAL_SALARY_DATA, REDACTED)),
                redact(SALARY_DATA, REDACTED),
                Optional.of(redact(DiffUtil.diff(ORIGINAL_SALARY_DATA, SALARY_DATA), REDACTED)),
                ENTITY_NAME
        );

        return salaryDataMapper.toDto(salaryDataRepository.save(SALARY_DATA), SALARY_DATA.getSalaryGrade());
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
}
