package dev.araopj.hrplatformapi.salary.service.impl;

import dev.araopj.hrplatformapi.audit.model.AuditAction;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import dev.araopj.hrplatformapi.salary.repository.SalaryDataRepository;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.mappers.SalaryDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalaryDataServiceImp Test")
class SalaryDataServiceImpTest {

    @Mock
    private SalaryGradeRepository salaryGradeRepository;
    @Mock
    private SalaryDataRepository salaryDataRepository;
    @Mock
    private SalaryDataMapper salaryDataMapper;
    @Mock
    private AuditUtil auditUtil;
    @InjectMocks
    private SalaryDataServiceImp salaryDataService;

    private SalaryGrade salaryGrade;
    private SalaryDataRequest salaryDataRequest;
    private SalaryData salaryData;
    private SalaryDataResponse salaryDataResponse;
    private Pageable pageable;

    private final Set<String> REDACTED = Set.of("id", "salaryGrade");
    private final String ENTITY_NAME = SalaryDataResponse.class.getName();

    @BeforeEach
    void setUp() {

        salaryGrade = SalaryGrade.builder()
                .id("salary-grade-id-1")
                .legalBasis("Legal Basis 1")
                .effectiveDate(LocalDate.of(2020, 1, 1))
                .salaryGrade(1)
                .build();

        salaryDataRequest = SalaryDataRequest.builder()
                .step(1)
                .amount(1000)
                .salaryGradeId(salaryGrade.getId())
                .build();

        salaryData = SalaryData.builder()
                .id("salary-data-id-1")
                .step(salaryDataRequest.step())
                .amount(salaryDataRequest.amount())
                .salaryGrade(salaryGrade)
                .build();

        salaryDataResponse = SalaryDataResponse.builder()
                .id(salaryData.getId())
                .step(salaryData.getStep())
                .amount(salaryData.getAmount())
                .build();

        pageable = Pageable.ofSize(10);
    }

    @Nested
    @DisplayName("Create Salary Data Tests")
    class CreateSalaryDataTest {

        @Test
        @DisplayName("Should create salary data when valid request")
        void shouldCreateSalaryDataWhenValidRequest() {
            when(salaryDataRepository.findByStepAndAmountAndSalaryGradeId(salaryDataRequest.step(), salaryDataRequest.amount(), salaryDataRequest.salaryGradeId()))
                    .thenReturn(Optional.empty());
            when(salaryGradeRepository.findById(salaryGrade.getId())).thenReturn(Optional.of(salaryGrade));
            when(salaryDataMapper.toEntity(salaryDataRequest, salaryGrade)).thenReturn(salaryData);

            when(salaryDataRepository.save(salaryData)).thenReturn(salaryData);
            when(salaryDataMapper.toDto(salaryData, salaryGrade)).thenReturn(salaryDataResponse);

            var salaryDataResponse = salaryDataService.create(salaryDataRequest);

            assertNotNull(salaryDataResponse);
            assertEquals(salaryData.getId(), salaryDataResponse.id());
            assertEquals(salaryData.getStep(), salaryDataResponse.step());
            assertEquals(salaryData.getAmount(), salaryDataResponse.amount());
            verify(auditUtil, times(1)).audit(AuditAction.CREATE,
                    salaryData.getId(),
                    Optional.empty(),
                    redact(salaryData, REDACTED),
                    Optional.empty(),
                    ENTITY_NAME
            );
        }

        @Test
        @DisplayName("Should fail if salary grade does not exist")
        void shouldFailIfSalaryGradeDoesNotExist() {
            when(salaryDataRepository.findByStepAndAmountAndSalaryGradeId(salaryDataRequest.step(), salaryDataRequest.amount(), salaryDataRequest.salaryGradeId()))
                    .thenReturn(Optional.empty());

            when(salaryGradeRepository.findById(salaryGrade.getId())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> salaryDataService.create(salaryDataRequest));
        }

        @Test
        @DisplayName("Should fail if salary data already exists")
        void shouldFailIfSalaryDataAlreadyExists() {
            when(salaryDataRepository.findByStepAndAmountAndSalaryGradeId(salaryDataRequest.step(), salaryDataRequest.amount(), salaryDataRequest.salaryGradeId()))
                    .thenReturn(Optional.of(salaryData));

            assertThrows(IllegalArgumentException.class, () -> salaryDataService.create(salaryDataRequest));
        }

        @Test
        @DisplayName("Should fail if amount is null")
        void shouldFailIfSalaryGradeIdIsNull() {
            var invalidRequest = SalaryDataRequest.builder()
                    .step(1)
                    .amount(1000)
                    .salaryGradeId(null)
                    .build();

            assertThrows(IllegalArgumentException.class, () -> salaryDataService.create(invalidRequest));
        }
    }

}