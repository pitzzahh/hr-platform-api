package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationSalaryOverrideRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationSalaryOverrideResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformationSalaryOverride;
import dev.araopj.hrplatformapi.employee.model.EmploymentStatus;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationSalaryOverrideRepository;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.EmploymentInformationSalaryOverrideMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.EMPLOYMENT_INFORMATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmploymentInformationSalaryOverrideServiceImp Test")
class EmploymentInformationSalaryOverrideServiceImpTest {

    @Mock
    private EmploymentInformationRepository employmentInformationRepository;
    @Mock
    private EmploymentInformationSalaryOverrideRepository employmentInformationSalaryOverrideRepository;
    @InjectMocks
    private EmploymentInformationSalaryOverrideServiceImp employmentInformationSalaryOverrideServiceImp;

    private EmploymentInformation employmentInformation;
    private EmploymentInformationSalaryOverride salaryOverride;
    private EmploymentInformationSalaryOverrideResponse salaryOverrideResponse;
    private EmploymentInformationSalaryOverrideRequest salaryOverrideRequest;

    @BeforeEach
    void setup() {
        employmentInformation = EmploymentInformation.builder()
                .id("emp-info-1")
                .employmentStatus(EmploymentStatus.PERMANENT)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 1, 2))
                .build();

        salaryOverride = EmploymentInformationSalaryOverride.builder()
                .id("salary-override-1")
                .salary(60000.0)
                .effectiveDate(LocalDate.of(2025, 1, 1))
                .employmentInformation(employmentInformation)
                .build();

        salaryOverrideResponse = EmploymentInformationSalaryOverrideResponse.builder()
                .id("salary-override-1")
                .salary(60000.0)
                .effectiveDate(LocalDate.of(2025, 1, 1))
                .build();

        salaryOverrideRequest = EmploymentInformationSalaryOverrideRequest.builder()
                .salary(60000.0)
                .effectiveDate(LocalDate.of(2025, 1, 1))
                .employmentInformationId("emp-info-1")
                .build();
    }

    @Nested
    @DisplayName("Create Salary Override Test")
    class CreateSalaryOverrideTest {

        @Test
        @DisplayName("Should create salary override successfully when valid request")
        void shouldCreateSalaryOverrideSuccessfullyWhenValidRequest() {
            try (var mapperMock = mockStatic(EmploymentInformationSalaryOverrideMapper.class)) {
                when(employmentInformationSalaryOverrideRepository.findBySalaryAndEffectiveDateAndEmploymentInformationId(
                        eq(salaryOverrideRequest.salary()), eq(salaryOverrideRequest.effectiveDate()), eq(salaryOverrideRequest.employmentInformationId())))
                        .thenReturn(Optional.empty());
                when(employmentInformationRepository.findById(salaryOverrideRequest.employmentInformationId()))
                        .thenReturn(Optional.of(employmentInformation));
                when(employmentInformationSalaryOverrideRepository.save(any(EmploymentInformationSalaryOverride.class)))
                        .thenReturn(salaryOverride);
                mapperMock.when(() -> EmploymentInformationSalaryOverrideMapper.toDto(salaryOverride))
                        .thenReturn(salaryOverrideResponse);

                var result = employmentInformationSalaryOverrideServiceImp.create(salaryOverrideRequest);

                assertNotNull(result);
                assertEquals(salaryOverrideResponse, result);
                assertEquals(salaryOverrideResponse.id(), result.id());
                assertEquals(salaryOverrideResponse.salary(), result.salary());
                assertEquals(salaryOverrideResponse.effectiveDate(), result.effectiveDate());

                verify(employmentInformationSalaryOverrideRepository).findBySalaryAndEffectiveDateAndEmploymentInformationId(
                        salaryOverrideRequest.salary(), salaryOverrideRequest.effectiveDate(), salaryOverrideRequest.employmentInformationId());
                verify(employmentInformationRepository).findById(salaryOverrideRequest.employmentInformationId());
                verify(employmentInformationSalaryOverrideRepository).save(any(EmploymentInformationSalaryOverride.class));
            }
        }

        @Test
        @DisplayName("Should throw exception when salary override already exists")
        void shouldThrowExceptionWhenSalaryOverrideAlreadyExists() {
            when(employmentInformationSalaryOverrideRepository.findBySalaryAndEffectiveDateAndEmploymentInformationId(
                    salaryOverrideRequest.salary(), salaryOverrideRequest.effectiveDate(), salaryOverrideRequest.employmentInformationId()))
                    .thenReturn(Optional.of(salaryOverride));

            var exception = assertThrows(IllegalArgumentException.class,
                    () -> employmentInformationSalaryOverrideServiceImp.create(salaryOverrideRequest));
            assertEquals(String.format("EmploymentInformationSalaryOverride already exists with salary %s, effectiveDate %s, for employment information with id [%s]",
                            salaryOverride.getSalary(), salaryOverride.getEffectiveDate(), salaryOverrideRequest.employmentInformationId()),
                    exception.getMessage());

            verify(employmentInformationSalaryOverrideRepository).findBySalaryAndEffectiveDateAndEmploymentInformationId(
                    salaryOverrideRequest.salary(), salaryOverrideRequest.effectiveDate(), salaryOverrideRequest.employmentInformationId());
            verifyNoMoreInteractions(employmentInformationSalaryOverrideRepository);
            verifyNoInteractions(employmentInformationRepository);
        }

        @Test
        @DisplayName("Should throw exception when employment information not found")
        void shouldThrowExceptionWhenEmploymentInformationNotFound() {
            when(employmentInformationSalaryOverrideRepository.findBySalaryAndEffectiveDateAndEmploymentInformationId(
                    salaryOverrideRequest.salary(), salaryOverrideRequest.effectiveDate(), salaryOverrideRequest.employmentInformationId()))
                    .thenReturn(Optional.empty());
            when(employmentInformationRepository.findById(salaryOverrideRequest.employmentInformationId()))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(NotFoundException.class,
                    () -> employmentInformationSalaryOverrideServiceImp.create(salaryOverrideRequest));
            assertEquals(new NotFoundException(employmentInformation.getId(), EMPLOYMENT_INFORMATION).getMessage(),
                    exception.getMessage());

            verify(employmentInformationSalaryOverrideRepository).findBySalaryAndEffectiveDateAndEmploymentInformationId(
                    salaryOverrideRequest.salary(), salaryOverrideRequest.effectiveDate(), salaryOverrideRequest.employmentInformationId());
            verify(employmentInformationRepository).findById(salaryOverrideRequest.employmentInformationId());
            verifyNoMoreInteractions(employmentInformationSalaryOverrideRepository);
            verifyNoMoreInteractions(employmentInformationRepository);
        }
    }

    @Nested
    @DisplayName("Find Salary Override Test")
    class FindSalaryOverrideTest {

        @Test
        @DisplayName("Should find all salary overrides")
        void shouldFindAllSalaryOverrides() {
            try (var mapperMock = mockStatic(EmploymentInformationSalaryOverrideMapper.class)) {
                var salaryOverrides = List.of(salaryOverride);
                when(employmentInformationSalaryOverrideRepository.findAll())
                        .thenReturn(salaryOverrides);
                mapperMock.when(() -> EmploymentInformationSalaryOverrideMapper.toDto(salaryOverride))
                        .thenReturn(salaryOverrideResponse);

                var result = employmentInformationSalaryOverrideServiceImp.findAll();

                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals(salaryOverrideResponse, result.getFirst());
            }
        }

        @Test
        @DisplayName("Should return empty list when no salary overrides exist")
        void shouldReturnEmptyListWhenNoSalaryOverridesExist() {
            try (var mapperMock = mockStatic(EmploymentInformationSalaryOverrideMapper.class)) {
                when(employmentInformationSalaryOverrideRepository.findAll())
                        .thenReturn(List.of());

                var result = employmentInformationSalaryOverrideServiceImp.findAll();

                assertNotNull(result);
                assertTrue(result.isEmpty());
                mapperMock.verifyNoInteractions();
            }
        }

        @Test
        @DisplayName("Should find salary override by id successfully when exists")
        void shouldFindSalaryOverrideByIdSuccessfullyWhenExists() {
            try (var mapperMock = mockStatic(EmploymentInformationSalaryOverrideMapper.class)) {
                when(employmentInformationSalaryOverrideRepository.findById(salaryOverride.getId()))
                        .thenReturn(Optional.of(salaryOverride));
                mapperMock.when(() -> EmploymentInformationSalaryOverrideMapper.toDto(salaryOverride))
                        .thenReturn(salaryOverrideResponse);

                var result = employmentInformationSalaryOverrideServiceImp.findById(salaryOverride.getId());

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(salaryOverrideResponse, result.get());
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when salary override not found by id")
        void shouldThrowNotFoundExceptionWhenSalaryOverrideNotFoundById() {
            when(employmentInformationSalaryOverrideRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> employmentInformationSalaryOverrideServiceImp.findById("non-existent-id"));

            verify(employmentInformationSalaryOverrideRepository).findById("non-existent-id");
        }
    }

    @Nested
    @DisplayName("Update Salary Override Test")
    class UpdateSalaryOverrideTest {

        @Test
        @DisplayName("Should update salary override successfully when valid request")
        void shouldUpdateSalaryOverrideSuccessfullyWhenValidRequest() {
            try (var mapperMock = mockStatic(EmploymentInformationSalaryOverrideMapper.class);
                 var mergeUtilMock = mockStatic(MergeUtil.class)) {
                var updatedRequest = EmploymentInformationSalaryOverrideRequest.builder()
                        .salary(70000.0)
                        .effectiveDate(LocalDate.of(2025, 2, 1))
                        .employmentInformationId("emp-info-1")
                        .build();
                var updatedSalaryOverride = EmploymentInformationSalaryOverride.builder()
                        .id("salary-override-1")
                        .salary(updatedRequest.salary())
                        .effectiveDate(updatedRequest.effectiveDate())
                        .employmentInformation(employmentInformation)
                        .build();
                var updatedResponse = EmploymentInformationSalaryOverrideResponse.builder()
                        .id("salary-override-1")
                        .salary(updatedRequest.salary())
                        .effectiveDate(updatedRequest.effectiveDate())
                        .build();

                when(employmentInformationSalaryOverrideRepository.findById(salaryOverride.getId()))
                        .thenReturn(Optional.of(salaryOverride));
                mapperMock.when(() -> EmploymentInformationSalaryOverrideMapper.toEntity(updatedRequest))
                        .thenReturn(updatedSalaryOverride);
                mergeUtilMock.when(() -> MergeUtil.merge(salaryOverride, updatedSalaryOverride))
                        .thenReturn(updatedSalaryOverride);
                when(employmentInformationSalaryOverrideRepository.save(updatedSalaryOverride)).thenReturn(updatedSalaryOverride);
                mapperMock.when(() -> EmploymentInformationSalaryOverrideMapper.toDto(updatedSalaryOverride))
                        .thenReturn(updatedResponse);

                var result = employmentInformationSalaryOverrideServiceImp.update(salaryOverride.getId(), updatedRequest);

                assertNotNull(result);
                assertEquals(updatedResponse, result);
                assertEquals(updatedResponse.salary(), result.salary());
                assertEquals(updatedResponse.effectiveDate(), result.effectiveDate());

                verify(employmentInformationSalaryOverrideRepository).findById(salaryOverride.getId());
                verify(employmentInformationSalaryOverrideRepository).save(updatedSalaryOverride);
            }
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when id is null")
        void shouldThrowInvalidRequestExceptionWhenIdIsNull() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> employmentInformationSalaryOverrideServiceImp.update(null, salaryOverrideRequest));

            assertEquals("EmploymentInformationSalaryOverride ID must be provided as path", exception.getMessage());
            verifyNoInteractions(employmentInformationSalaryOverrideRepository);
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when id is empty")
        void shouldThrowInvalidRequestExceptionWhenIdIsEmpty() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> employmentInformationSalaryOverrideServiceImp.update("", salaryOverrideRequest));

            assertEquals("EmploymentInformationSalaryOverride ID must be provided as path", exception.getMessage());
            verifyNoInteractions(employmentInformationSalaryOverrideRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when salary override not found")
        void shouldThrowNotFoundExceptionWhenSalaryOverrideNotFound() {
            when(employmentInformationSalaryOverrideRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> employmentInformationSalaryOverrideServiceImp.update("non-existent-id", salaryOverrideRequest));

            verify(employmentInformationSalaryOverrideRepository).findById("non-existent-id");
            verifyNoMoreInteractions(employmentInformationSalaryOverrideRepository);
        }
    }

    @Nested
    @DisplayName("Delete Salary Override Test")
    class DeleteSalaryOverrideTest {

        @Test
        @DisplayName("Should delete salary override successfully when exists")
        void shouldDeleteSalaryOverrideSuccessfullyWhenExists() {
            try (var mapperMock = mockStatic(EmploymentInformationSalaryOverrideMapper.class)) {
                when(employmentInformationSalaryOverrideRepository.findById(salaryOverride.getId()))
                        .thenReturn(Optional.of(salaryOverride));
                mapperMock.when(() -> EmploymentInformationSalaryOverrideMapper.toDto(salaryOverride))
                        .thenReturn(salaryOverrideResponse);
                when(employmentInformationSalaryOverrideRepository.existsById(salaryOverride.getId()))
                        .thenReturn(false);

                var result = employmentInformationSalaryOverrideServiceImp.delete(salaryOverride.getId());

                assertTrue(result);
                verify(employmentInformationSalaryOverrideRepository).findById(salaryOverride.getId());
                verify(employmentInformationSalaryOverrideRepository).deleteById(salaryOverride.getId());
                verify(employmentInformationSalaryOverrideRepository).existsById(salaryOverride.getId());
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when salary override not found")
        void shouldThrowNotFoundExceptionWhenSalaryOverrideNotFound() {
            when(employmentInformationSalaryOverrideRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> employmentInformationSalaryOverrideServiceImp.delete("non-existent-id"));

            verify(employmentInformationSalaryOverrideRepository).findById("non-existent-id");
            verifyNoMoreInteractions(employmentInformationSalaryOverrideRepository);
        }

        @Test
        @DisplayName("Should return false when salary override not deleted")
        void shouldReturnFalseWhenSalaryOverrideNotDeleted() {
            try (var mapperMock = mockStatic(EmploymentInformationSalaryOverrideMapper.class)) {
                when(employmentInformationSalaryOverrideRepository.findById(salaryOverride.getId()))
                        .thenReturn(Optional.of(salaryOverride));
                mapperMock.when(() -> EmploymentInformationSalaryOverrideMapper.toDto(salaryOverride))
                        .thenReturn(salaryOverrideResponse);
                when(employmentInformationSalaryOverrideRepository.existsById(salaryOverride.getId()))
                        .thenReturn(true);

                var result = employmentInformationSalaryOverrideServiceImp.delete(salaryOverride.getId());

                assertFalse(result);
                verify(employmentInformationSalaryOverrideRepository).findById(salaryOverride.getId());
                verify(employmentInformationSalaryOverrideRepository).deleteById(salaryOverride.getId());
                verify(employmentInformationSalaryOverrideRepository).existsById(salaryOverride.getId());
            }
        }
    }
}