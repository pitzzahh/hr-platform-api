package dev.araopj.hrplatformapi.salary.service.impl;

import dev.araopj.hrplatformapi.audit.model.AuditAction;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.mappers.SalaryDataMapper;
import dev.araopj.hrplatformapi.utils.mappers.SalaryGradeMapper;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalaryGradeServiceImp Unit Test")
class SalaryGradeServiceImpTest {

    @Mock
    private SalaryGradeRepository salaryGradeRepository;

    @Mock
    private SalaryGradeMapper salaryGradeMapper;

    @Mock
    private SalaryDataMapper salaryDataMapper;

    @Mock
    private AuditUtil auditUtil;

    @InjectMocks
    private SalaryGradeServiceImp salaryGradeService;

    private SalaryGradeRequest salaryGradeRequest;
    private SalaryGrade salaryGrade;
    private SalaryGradeResponse salaryGradeResponse;
    private Pageable pageable;

    @BeforeEach
    void setup() {
        salaryGradeRequest = SalaryGradeRequest.builder()
                .legalBasis("NBC 591")
                .salaryGrade(1)
                .effectiveDate(LocalDate.of(2020, 1, 1))
                .salaryData(null) // Set to null to avoid validation issues when includeSalaryData is false
                .build();

        salaryGrade = SalaryGrade.builder()
                .id("1")
                .legalBasis("NBC 591")
                .salaryGrade(1)
                .effectiveDate(LocalDate.of(2020, 1, 1))
                .salaryData(null)
                .build();

        salaryGradeResponse = SalaryGradeResponse.builder()
                .id("1")
                .legalBasis("NBC 591")
                .salaryGrade(1)
                .effectiveDate(LocalDate.of(2020, 1, 1))
                .build();

        pageable = Pageable.ofSize(10);
    }

    @Nested
    @DisplayName("Create Salary Grade Tests")
    class CreateSalaryGradeTests {

        @Test
        @DisplayName("Should create salary grade without salary data")
        void shouldCreateSalaryGradeSuccessfullyWithoutSalaryData() throws BadRequestException {
            when(salaryGradeRepository.findBySalaryGradeAndEffectiveDate(anyInt(), any())).thenReturn(Optional.empty());
            when(salaryGradeMapper.toEntity(any(SalaryGradeRequest.class))).thenReturn(salaryGrade);
            when(salaryGradeRepository.saveAll(anyList())).thenReturn(List.of(salaryGrade));
            when(salaryGradeMapper.toDto(any(SalaryGrade.class), eq(false))).thenReturn(salaryGradeResponse);

            var salaryGradeResponses = salaryGradeService.create(List.of(salaryGradeRequest), false);

            assertNotNull(salaryGradeResponses);
            assertEquals(1, salaryGradeResponses.size());
            verify(auditUtil, times(1)).audit(eq(AuditAction.CREATE), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should create salary grade with salary data")
        void shouldCreateSalaryGradeSuccessfullyWithSalaryData() throws BadRequestException {
            var requestWithData = SalaryGradeRequest.builder()
                    .legalBasis("NBC 591")
                    .salaryGrade(1)
                    .effectiveDate(LocalDate.of(2020, 1, 1))
                    .salaryData(List.of(SalaryDataRequest.WithoutSalaryGradeId
                            .builder()
                            .step(1)
                            .amount(1000.0)
                            .build())
                    ).build();
            when(salaryGradeRepository.findBySalaryGradeAndEffectiveDate(anyInt(), any())).thenReturn(Optional.empty());
            when(salaryGradeMapper.toEntity(any(SalaryGradeRequest.class))).thenReturn(salaryGrade);
            when(salaryDataMapper.toEntity(any(SalaryDataRequest.WithoutSalaryGradeId.class))).thenReturn(new SalaryData());
            when(salaryGradeRepository.saveAll(anyList())).thenReturn(List.of(salaryGrade));
            when(salaryGradeMapper.toDto(any(SalaryGrade.class), eq(true))).thenReturn(salaryGradeResponse);

            var result = salaryGradeService.create(List.of(requestWithData), true);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(auditUtil, times(1)).audit(eq(AuditAction.CREATE), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for existing salary grade")
        void shouldThrowIllegalArgumentExceptionForExistingSalaryGrade() {
            when(salaryGradeRepository.findBySalaryGradeAndEffectiveDate(anyInt(), any())).thenReturn(Optional.of(salaryGrade));

            assertThrows(IllegalArgumentException.class, () -> salaryGradeService.create(List.of(salaryGradeRequest), false));
        }

        @Test
        @DisplayName("Should throw IllegalStateException when salary data is missing")
        void shouldThrowIllegalStateExceptionWhenSalaryDataMissing() {
            when(salaryGradeRepository.findBySalaryGradeAndEffectiveDate(anyInt(), any())).thenReturn(Optional.empty());

            assertThrows(IllegalStateException.class, () -> salaryGradeService.create(List.of(salaryGradeRequest), true));
        }
    }

    @Nested
    @DisplayName("Find All Salary Grades Tests")
    class FindAllSalaryGradesTests {

        @Test
        @DisplayName("Should return all salary grades without salary data")
        void shouldReturnAllSalaryGradesWithoutSalaryData() {
            var page = new PageImpl<>(List.of(salaryGrade));
            when(salaryGradeRepository.findAll(pageable)).thenReturn(page);
            when(salaryGradeMapper.toDto(any(SalaryGrade.class), eq(false))).thenReturn(salaryGradeResponse);

            var salaryGradeResponses = salaryGradeService.findAll(pageable, false);

            assertNotNull(salaryGradeResponses);
            assertEquals(1, salaryGradeResponses.getContent().size());
            verify(auditUtil, times(1)).audit(eq(AuditAction.VIEW), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should return all salary grades with salary data")
        void shouldReturnAllSalaryGradesWithSalaryData() {
            var page = new PageImpl<>(List.of(salaryGrade));
            when(salaryGradeRepository.findAllWithSalaryData(pageable)).thenReturn(page);
            when(salaryGradeMapper.toDto(any(SalaryGrade.class), eq(true))).thenReturn(salaryGradeResponse);

            var salaryGradeResponses = salaryGradeService.findAll(pageable, true);

            assertNotNull(salaryGradeResponses);
            assertEquals(1, salaryGradeResponses.getContent().size());
            verify(auditUtil, times(1)).audit(eq(AuditAction.VIEW), any(), any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Find By ID Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return salary grade by ID")
        void shouldReturnSalaryGradeById() throws BadRequestException {
            when(salaryGradeRepository.findById("1")).thenReturn(Optional.of(salaryGrade));
            when(salaryGradeMapper.toDto(any(SalaryGrade.class), eq(false))).thenReturn(salaryGradeResponse);

            var optionalSalaryGradeResponse = salaryGradeService.findById("1", false);

            assertTrue(optionalSalaryGradeResponse.isPresent());
            assertEquals("1", optionalSalaryGradeResponse.get().id());
            verify(auditUtil, times(1)).audit(eq("1"), any());
        }

        @Test
        @DisplayName("Should throw BadRequestException for null ID")
        void shouldThrowBadRequestExceptionForNullId() {
            assertThrows(BadRequestException.class, () -> salaryGradeService.findById(null, false));
        }

        @Test
        @DisplayName("Should throw BadRequestException for empty ID")
        void shouldThrowBadRequestExceptionForEmptyId() {
            assertThrows(BadRequestException.class, () -> salaryGradeService.findById("", false));
        }

        @Test
        @DisplayName("Should return empty for non-existent ID")
        void shouldReturnEmptyForNonExistentId() throws BadRequestException {
            when(salaryGradeRepository.findById("1")).thenReturn(Optional.empty());

            var optionalSalaryGradeResponse = salaryGradeService.findById("1", false);

            assertTrue(optionalSalaryGradeResponse.isEmpty());
            verify(auditUtil, times(1)).audit(eq("1"), any());
        }
    }

    @Nested
    @DisplayName("Update Salary Grade Tests")
    class UpdateSalaryGradeTests {

        @Test
        @DisplayName("Should update salary grade successfully")
        void shouldUpdateSalaryGradeSuccessfully() throws BadRequestException {
            when(salaryGradeRepository.findById("1")).thenReturn(Optional.of(salaryGrade));
            when(salaryGradeMapper.toEntity(any(SalaryGradeRequest.class))).thenReturn(salaryGrade);
            when(salaryGradeRepository.saveAndFlush(any(SalaryGrade.class))).thenReturn(salaryGrade);
            when(salaryGradeMapper.toDto(any(SalaryGrade.class), eq(false))).thenReturn(salaryGradeResponse);

            var updateResponse = salaryGradeService.update("1", salaryGradeRequest);

            assertNotNull(updateResponse);
            assertEquals("1", updateResponse.id());
            verify(auditUtil, times(1)).audit(eq(AuditAction.UPDATE), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should throw BadRequestException for null ID")
        void shouldThrowBadRequestExceptionForNullId() {
            assertThrows(BadRequestException.class, () -> salaryGradeService.update(null, salaryGradeRequest));
        }

        @Test
        @DisplayName("Should throw NotFoundException for non-existent ID")
        void shouldThrowNotFoundExceptionForNonExistentId() {
            when(salaryGradeRepository.findById("1")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> salaryGradeService.update("1", salaryGradeRequest));
        }
    }

    @Nested
    @DisplayName("Delete Salary Grade Tests")
    class DeleteSalaryGradeTests {

        @Test
        @DisplayName("Should delete salary grade successfully")
        void shouldDeleteSalaryGradeSuccessfully() {
            when(salaryGradeRepository.findById("1")).thenReturn(Optional.of(salaryGrade));
            when(salaryGradeMapper.toDto(any(SalaryGrade.class), eq(false))).thenReturn(salaryGradeResponse);

            var isDeleted = salaryGradeService.delete("1");

            assertTrue(isDeleted);
            verify(salaryGradeRepository, times(1)).deleteById("1");
            verify(auditUtil, times(1)).audit(eq(AuditAction.DELETE), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should throw NotFoundException for non-existent ID when deleting")
        void shouldThrowNotFoundExceptionForNonExistentIdWhenDeleting() {
            when(salaryGradeRepository.findById("1")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> salaryGradeService.delete("1"));
        }
    }
}