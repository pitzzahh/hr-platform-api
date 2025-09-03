package dev.araopj.hrplatformapi.salary.service;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.repository.SalaryDataRepository;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.utils.*;
import dev.araopj.hrplatformapi.utils.enums.CheckType;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;
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
    public Page<SalaryDataResponse> findAll(int limit) {

        final var PAGEABLE = PageRequest.of(0, limit - 1);

        final var SALARY_DATA = salaryDataRepository.findAll(PAGEABLE);

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
            SalaryDataRequest salaryDataRequest,
            @Nullable String salaryGradeId, // salary grade id from request param
            CheckType checkType
    ) throws BadRequestException {
        final var OPTIONAL_SALARY_GRADE_CHECK = switch (checkType) {
            case CHECK_PARENT_FROM_REQUEST_PARAM -> {
                if (salaryGradeId == null || salaryGradeId.isEmpty()) {
                    throw new BadRequestException("Salary grade ID must be provided as query parameter when using CHECK_PARENT_FROM_REQUEST_PARAM.");
                }
                yield salaryGradeRepository.findById(salaryGradeId);
            }
            case CHECK_PARENT_FROM_REQUEST_BODY -> {
                if (salaryDataRequest.salaryGradeId() == null || salaryDataRequest.salaryGradeId().isEmpty()) {
                    throw new BadRequestException("Salary grade ID must be provided in request body when using CHECK_PARENT_FROM_REQUEST_BODY.");
                }
                yield salaryGradeRepository.findById(salaryDataRequest.salaryGradeId());
            }
        };


        final var SALARY_GRADE_ID_TO_CHECK = getId(salaryDataRequest, salaryGradeId, checkType);
        final var RESOLVED_SALARY_GRADE = OPTIONAL_SALARY_GRADE_CHECK.orElseThrow(() -> new NotFoundException(SALARY_GRADE_ID_TO_CHECK, SALARY_GRADE));

        salaryDataRepository.findByStepAndAmountAndSalaryGradeId(salaryDataRequest.step(), salaryDataRequest.amount(), SALARY_GRADE_ID_TO_CHECK)
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
        return Mapper.toDto(salaryDataRepository.save(SALARY_DATA_TO_SAVE));

    }

    @Override
    public SalaryDataResponse update(
            String id,
            SalaryDataRequest salaryDataRequest
    ) throws BadRequestException {

        if (id == null || id.isEmpty()) {
            throw new BadRequestException("SalaryData ID must be provided as path");
        }

        final var SALARY_GRADE_ID = salaryDataRequest.salaryGradeId();
        final var ORIGINAL_SALARY_DATA = salaryDataRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, SALARY_DATA));
        var SALARY_DATA = MergeUtil.merge(ORIGINAL_SALARY_DATA,
                Mapper.toEntity(salaryDataRequest)
        );

        if (SALARY_GRADE_ID != null && !SALARY_GRADE_ID.isEmpty()) {
            final var SALARY_GRADE_PARENT = findByIdAndSalaryGradeId(id, SALARY_GRADE_ID)
                    .orElseThrow(() -> new NotFoundException(id, SALARY_GRADE_ID, NotFoundException.EntityType.SALARY_DATA, "salaryGradeId"));
            if (!Objects.equals(SALARY_GRADE_PARENT.id(), SALARY_DATA.getSalaryGrade().getId())) {
                throw new BadRequestException("SalaryData with id %s does not belong to SalaryGrade with id %s".formatted(id, SALARY_GRADE_ID));
            }

            // check for existing entity based on new update values
            salaryDataRepository.findByStepAndAmountAndSalaryGradeId(
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
                Optional.of(redact(ORIGINAL_SALARY_DATA, REDACTED)),
                redact(SALARY_DATA, REDACTED),
                Optional.of(redact(DiffUtil.diff(ORIGINAL_SALARY_DATA, SALARY_DATA), REDACTED)),
                ENTITY_NAME
        );

        return Mapper.toDto(salaryDataRepository.saveAndFlush(SALARY_DATA));
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

    private static String getId(SalaryDataRequest request, String salaryGradeId, CheckType checkType) throws
            BadRequestException {
        switch (checkType) {
            case CHECK_PARENT_FROM_REQUEST_PARAM -> {
                if (salaryGradeId == null || salaryGradeId.isEmpty())
                    throw new BadRequestException("Salary grade ID must be provided as query parameter when using CHECK_PARENT_FROM_REQUEST_PARAM.");
                return salaryGradeId;
            }
            case CHECK_PARENT_FROM_REQUEST_BODY -> {
                if (request.salaryGradeId() == null || request.salaryGradeId().isEmpty())
                    throw new BadRequestException("Salary grade ID must be provided in request body when using CHECK_PARENT_FROM_REQUEST_BODY.");
                return request.salaryGradeId();
            }
            default -> throw new BadRequestException("Invalid CheckType provided.");
        }
    }
}
