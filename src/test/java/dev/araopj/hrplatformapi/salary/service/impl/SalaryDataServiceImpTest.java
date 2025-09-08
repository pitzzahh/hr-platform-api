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
import dev.araopj.hrplatformapi.utils.DiffUtil;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.SalaryDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

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
    private SalaryDataRequest.WithoutSalaryGradeId salaryDataRequestWithoutSalaryGradeId;
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

        salaryDataRequestWithoutSalaryGradeId = SalaryDataRequest
                .WithoutSalaryGradeId
                .builder()
                .step(1)
                .amount(1000)
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
        @DisplayName("Should fail if salary grade is null")
        void shouldFailIfSalaryGradeIdIsNull() {
            var invalidRequest = SalaryDataRequest.builder()
                    .step(1)
                    .amount(1000)
                    .salaryGradeId(null)
                    .build();

            assertThrows(IllegalArgumentException.class, () -> salaryDataService.create(invalidRequest));
        }

        @Test
        @DisplayName("Should fail if request is null")
        void shouldHandleNullRequest() {
            assertThrows(IllegalArgumentException.class, () -> salaryDataService.create(null));
        }
    }

    @Nested
    @DisplayName("Find Salary Data Tests")
    class FindSalaryDataTest {

        @Test
        @DisplayName("Should find salary data when exists")
        void shouldFindSalaryDataWhenExists() {
            when(salaryDataRepository.findByIdAndSalaryGradeId(salaryData.getId(), salaryGrade.getId()))
                    .thenReturn(Optional.of(salaryData));
            when(salaryDataMapper.toDto(salaryData, salaryGrade)).thenReturn(salaryDataResponse);

            var result = salaryDataService.findByIdAndSalaryGradeId(salaryData.getId(), salaryGrade.getId());

            System.out.println("result = " + result);
            assertNotNull(result);
            assertTrue(result.isPresent());
            assertEquals(salaryDataResponse, result.get());
            verify(auditUtil, times(1))
                    .audit(
                            AuditAction.VIEW,
                            salaryData.getId(),
                            Optional.of(redact(salaryData, REDACTED)),
                            Optional.empty(),
                            Optional.empty(),
                            ENTITY_NAME
                    );
        }

        @Test
        @DisplayName("Should throw NotFoundException if salary data does not exist")
        void shouldThrowNotFoundExceptionIfSalaryDataDoesNotExist() {
            when(salaryDataRepository.findByIdAndSalaryGradeId(salaryData.getId(), salaryGrade.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> salaryDataService.findByIdAndSalaryGradeId(salaryData.getId(), salaryGrade.getId()));
        }

        @Test
        @DisplayName("Should get all salary data when exists")
        void shouldGetAllSalaryDataWhenExists() {
            var page = new PageImpl<>(List.of(salaryData));
            when(salaryDataRepository.findAll(pageable)).thenReturn(page);
            when(salaryDataMapper.toDto(salaryData, salaryGrade)).thenReturn(salaryDataResponse);

            var salaryDataResponsePage = salaryDataService.findAll(pageable);

            assertNotNull(salaryDataResponsePage);
            assertEquals(1, salaryDataResponsePage.getContent().size());
            assertEquals(salaryDataResponse, salaryDataResponsePage.getContent().getFirst());
            verify(auditUtil, times(1)).audit(
                    AuditAction.VIEW,
                    "[]",
                    Optional.of(redact(salaryDataResponsePage.getContent(), REDACTED)),
                    Optional.empty(),
                    Optional.empty(),
                    "Page<%s>".formatted(ENTITY_NAME)
            );
        }
    }

    @Nested
    @DisplayName("SalaryDataServiceImp FindById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find by id when exists")
        void shouldFindByIdWhenExists() {
            when(salaryDataRepository.findById(salaryData.getId()))
                    .thenReturn(Optional.of(salaryData));
            when(salaryDataMapper.toDto(salaryData, salaryGrade)).thenReturn(salaryDataResponse);

            var result = salaryDataService.findById(salaryData.getId());

            assertNotNull(result);
            assertTrue(result.isPresent());
            assertEquals(salaryDataResponse, result.get());
            verify(auditUtil, times(1))
                    .audit(
                            salaryData.getId(),
                            ENTITY_NAME
                    );
        }

        @Test
        @DisplayName("Should throw NotFoundException if salary data does not exist")
        void shouldThrowNotFoundExceptionIfSalaryDataDoesNotExist() {
            when(salaryDataRepository.findById(salaryData.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> salaryDataService.findById(salaryData.getId()));
        }

        @Test
        @DisplayName("Should handle null id")
        void shouldHandleNullId() {
            assertThrows(IllegalArgumentException.class, () -> salaryDataService.findById(null));
        }

        @Test
        @DisplayName("Should handle empty id")
        void shouldHandleEmptyId() {
            assertThrows(IllegalArgumentException.class, () -> salaryDataService.findById(""));
        }

        @Test
        @DisplayName("Should return empty optional when id not found")
        void shouldReturnEmptyOptionalWhenIdNotFound() {
            when(salaryDataRepository.findById("non-existent-id")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> salaryDataService.findById("non-existent-id"));

            verify(auditUtil, times(1))
                    .audit(
                            "non-existent-id",
                            ENTITY_NAME
                    );
        }
    }

    @Nested
    @DisplayName("Update Salary Data Tests")
    class UpdateSalaryDataTest {

        @Test
        @DisplayName("Should update salary data successfully when valid request")
        void shouldUpdateSalaryDataSuccessfullyWhenValidRequest() {
            var salaryDataRequest = SalaryDataRequest
                    .WithoutSalaryGradeId
                    .builder()
                    .step(2)
                    .amount(2000)
                    .build();

            var entityRequest = SalaryData.builder()
                    .step(salaryDataRequest.step())
                    .amount(salaryDataRequest.amount())
                    .build();

            var salaryDataToUpdate = SalaryData.builder()
                    .id(salaryData.getId())
                    .step(salaryDataRequest.step())
                    .amount(salaryDataRequest.amount())
                    .salaryGrade(salaryGrade)
                    .build();

            var updatedSalaryDataResponse = SalaryDataResponse.builder()
                    .id(salaryData.getId())
                    .step(salaryDataRequest.step())
                    .amount(salaryDataRequest.amount())
                    .build();

            var diffResult = new HashMap<>(
                    Map.of(
                            "step", List.of(salaryData.getStep(), salaryDataRequest.step()),
                            "amount", List.of(salaryData.getAmount(), salaryDataRequest.amount())
                    )
            );

            try (MockedStatic<MergeUtil> mergeUtilMock = mockStatic(MergeUtil.class);
                 MockedStatic<DiffUtil> diffUtilMock = mockStatic(DiffUtil.class)) {
                mergeUtilMock.when(() -> MergeUtil.merge(salaryData, entityRequest))
                        .thenReturn(salaryDataToUpdate);
                diffUtilMock.when(() -> DiffUtil.diff(salaryData, salaryDataToUpdate))
                        .thenReturn(diffResult);

                when(salaryDataRepository.findById(salaryData.getId())).thenReturn(Optional.of(salaryData));
                when(salaryDataMapper.toEntity(salaryDataRequest)).thenReturn(entityRequest);
                when(salaryDataRepository.save(salaryDataToUpdate)).thenReturn(salaryDataToUpdate);
                when(salaryDataMapper.toDto(salaryDataToUpdate, salaryGrade)).thenReturn(updatedSalaryDataResponse);

                var salaryDataUpdateResult = salaryDataService.update(salaryData.getId(), salaryDataRequest);

                assertNotNull(salaryDataUpdateResult);
                assertEquals(updatedSalaryDataResponse, salaryDataUpdateResult);
                assertEquals(salaryDataRequest.step(), salaryDataUpdateResult.step());
                assertEquals(salaryDataRequest.amount(), salaryDataUpdateResult.amount());
                verify(auditUtil, times(1)).audit(
                        AuditAction.UPDATE,
                        salaryData.getId(),
                        Optional.of(redact(salaryData, REDACTED)),
                        redact(salaryDataToUpdate, REDACTED),
                        Optional.of(redact(diffResult, REDACTED)),
                        ENTITY_NAME
                );
                verify(salaryDataRepository).findById(salaryData.getId());
                verify(salaryDataRepository).save(salaryDataToUpdate);
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException if salary data does not exist")
        void shouldHandleNullId() {
            assertThrows(IllegalArgumentException.class, () -> salaryDataService.update(null, salaryDataRequestWithoutSalaryGradeId));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException if id is empty")
        void shouldThrowIllegalArgumentExceptionIfIdIsEmpty() {
            assertThrows(IllegalArgumentException.class, () -> salaryDataService.update("", salaryDataRequestWithoutSalaryGradeId));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException if request is null")
        void shouldThrowIllegalArgumentExceptionIfRequestIsNull() {
            assertThrows(IllegalArgumentException.class, () -> salaryDataService.update(salaryData.getId(), null));
        }

        @Test
        @DisplayName("Should throw NotFoundException if salary data to update does not exist")
        void shouldThrowNotFoundExceptionIfSalaryDataToUpdateDoesNotExist() {
            when(salaryDataRepository.findById(salaryData.getId())).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> salaryDataService.update(salaryData.getId(), salaryDataRequestWithoutSalaryGradeId));

            verify(salaryDataRepository).findById(salaryData.getId());
            verify(salaryDataRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Salary Data Tests")
    class DeleteSalaryDataTest {

        @Test
        @DisplayName("Should delete salary data successfully when exists")
        void shouldDeleteSalaryDataSuccessfullyWhenExists() {
            when(salaryDataRepository.findByIdAndSalaryGradeId(salaryData.getId(), salaryGrade.getId()))
                    .thenReturn(Optional.of(salaryData));
            when(salaryDataMapper.toDto(salaryData, salaryGrade)).thenReturn(salaryDataResponse);

            doNothing().when(salaryDataRepository).deleteById(salaryData.getId());

            var result = salaryDataService.delete(salaryData.getId(), salaryGrade.getId());

            assertTrue(result);
            verify(auditUtil, times(1))
                    .audit(
                            AuditAction.VIEW,
                            salaryData.getId(),
                            Optional.of(redact(salaryData, REDACTED)),
                            Optional.empty(),
                            Optional.empty(),
                            ENTITY_NAME
                    );
            verify(auditUtil, times(1)).audit(
                    AuditAction.DELETE,
                    salaryData.getId(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    ENTITY_NAME
            );
            verify(salaryDataRepository).findByIdAndSalaryGradeId(salaryData.getId(), salaryGrade.getId());
            verify(salaryDataRepository).deleteById(salaryData.getId());
        }

        @Test
        @DisplayName("Should throw NotFoundException if salary data does not exist")
        void shouldThrowNotFoundExceptionIfSalaryDataDoesNotExist() {
            when(salaryDataRepository.findByIdAndSalaryGradeId(salaryData.getId(), salaryGrade.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> salaryDataService.delete(salaryData.getId(), salaryGrade.getId()));

            verify(salaryDataRepository).findByIdAndSalaryGradeId(salaryData.getId(), salaryGrade.getId());
            verify(salaryDataRepository, never()).deleteById(any());
        }
    }

}