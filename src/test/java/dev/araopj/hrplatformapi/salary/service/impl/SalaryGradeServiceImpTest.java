package dev.araopj.hrplatformapi.salary.service.impl;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
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
                .salaryData(null)
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
                            .build()))
                    .build();
            when(salaryGradeRepository.findBySalaryGradeAndEffectiveDate(anyInt(), any())).thenReturn(Optional.empty());
            when(salaryGradeMapper.toEntity(any(SalaryGradeRequest.class))).thenReturn(salaryGrade);
            when(salaryDataMapper.toEntity(any(SalaryDataRequest.WithoutSalaryGradeId.class))).thenReturn(new SalaryData());
            when(salaryGradeRepository.saveAll(anyList())).thenReturn(List.of(salaryGrade));
            when(salaryGradeMapper.toDto(any(SalaryGrade.class), eq(true))).thenReturn(salaryGradeResponse);

            var result = salaryGradeService.create(List.of(requestWithData), true);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should create multiple salary grades with mixed salary data")
        void shouldCreateMultipleSalaryGradesWithMixedSalaryData() throws BadRequestException {
            var requestWithData = SalaryGradeRequest.builder()
                    .legalBasis("NBC 591")
                    .salaryGrade(1)
                    .effectiveDate(LocalDate.of(2020, 1, 1))
                    .salaryData(List.of(SalaryDataRequest.WithoutSalaryGradeId
                            .builder()
                            .step(1)
                            .amount(1000.0)
                            .build()))
                    .build();
            var requestWithoutData = SalaryGradeRequest.builder()
                    .legalBasis("NBC 592")
                    .salaryGrade(2)
                    .effectiveDate(LocalDate.of(2021, 1, 1))
                    .salaryData(null)
                    .build();
            var salaryGrade2 = SalaryGrade.builder()
                    .id("2")
                    .legalBasis("NBC 592")
                    .salaryGrade(2)
                    .effectiveDate(LocalDate.of(2021, 1, 1))
                    .salaryData(null)
                    .build();
            var salaryGradeResponse2 = SalaryGradeResponse.builder()
                    .id("2")
                    .legalBasis("NBC 592")
                    .salaryGrade(2)
                    .effectiveDate(LocalDate.of(2021, 1, 1))
                    .build();

            when(salaryGradeRepository.findBySalaryGradeAndEffectiveDate(anyInt(), any()))
                    .thenReturn(Optional.empty());
            when(salaryGradeMapper.toEntity(eq(requestWithData))).thenReturn(salaryGrade);
            when(salaryGradeMapper.toEntity(eq(requestWithoutData))).thenReturn(salaryGrade2);
            when(salaryGradeRepository.saveAll(anyList())).thenReturn(List.of(salaryGrade, salaryGrade2));
            when(salaryGradeMapper.toDto(eq(salaryGrade), eq(false))).thenReturn(salaryGradeResponse);
            when(salaryGradeMapper.toDto(eq(salaryGrade2), eq(false))).thenReturn(salaryGradeResponse2);

            var result = salaryGradeService.create(List.of(requestWithData, requestWithoutData), false);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("1", result.get(0).id());
            assertEquals("2", result.get(1).id());
            verify(salaryGradeRepository, times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("Should handle empty salary grade request list")
        void shouldHandleEmptySalaryGradeRequestList() throws BadRequestException {
            var result = salaryGradeService.create(List.of(), false);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(salaryGradeRepository, never()).saveAll(anyList());
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
        }
    }

    @Nested
    @DisplayName("Find By ID Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return salary grade by ID")
        void shouldReturnSalaryGradeById() {
            when(salaryGradeRepository.findById("1")).thenReturn(Optional.of(salaryGrade));
            when(salaryGradeMapper.toDto(any(SalaryGrade.class), eq(false))).thenReturn(salaryGradeResponse);

            var optionalSalaryGradeResponse = salaryGradeService.findById("1", false);

            assertTrue(optionalSalaryGradeResponse.isPresent());
            assertEquals("1", optionalSalaryGradeResponse.get().id());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for null ID")
        void shouldThrowIllegalArgumentExceptionForNullId() {
            assertThrows(IllegalArgumentException.class, () -> salaryGradeService.findById(null, false));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for empty ID")
        void shouldThrowIllegalArgumentExceptionForEmptyId() {
            assertThrows(IllegalArgumentException.class, () -> salaryGradeService.findById("", false));
        }

        @Test
        @DisplayName("Should throw NotFoundException when non-existing ID")
        void shouldThrowNotFoundExceptionWhenNonExistingId() {
            when(salaryGradeRepository.findById("1")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> salaryGradeService.findById("1", false));
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
        }

        @Test
        @DisplayName("Should throw NotFoundException for non-existent ID when deleting")
        void shouldThrowNotFoundExceptionForNonExistentIdWhenDeleting() {
            when(salaryGradeRepository.findById("1")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> salaryGradeService.delete("1"));
        }
    }
}