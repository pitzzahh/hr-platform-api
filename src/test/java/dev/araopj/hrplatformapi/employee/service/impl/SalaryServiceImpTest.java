package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.SalaryRequest;
import dev.araopj.hrplatformapi.employee.dto.response.SalaryResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import dev.araopj.hrplatformapi.employee.model.EmploymentStatus;
import dev.araopj.hrplatformapi.employee.model.Salary;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.SalaryRepository;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.SalaryMapper;
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

import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.EMPLOYMENT_INFORMATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalaryServiceImp Test")
class SalaryServiceImpTest {

    @Mock
    private EmploymentInformationRepository employmentInformationRepository;
    @Mock
    private SalaryRepository salaryRepository;
    @InjectMocks
    private SalaryServiceImp salaryServiceImp;

    private EmploymentInformation employmentInformation;
    private Salary salary;
    private SalaryResponse salaryResponse;
    private SalaryRequest salaryRequest;
    private Pageable pageable;

    @BeforeEach
    void setup() {
        salary = Salary.builder()
                .id("salary-1")
                .amount(50000.0)
                .currency("PHP")
                .employmentInformation(null)
                .build();

        salaryResponse = SalaryResponse.builder()
                .id("salary-1")
                .amount(50000.0)
                .currency("PHP")
                .build();

        salaryRequest = SalaryRequest.builder()
                .amount(50000.0)
                .currency("PHP")
                .employmentInformationId("emp-info-1")
                .build();

        employmentInformation = EmploymentInformation.builder()
                .id("emp-info-1")
                .employmentStatus(EmploymentStatus.PERMANENT)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 1, 2))
                .build();

        pageable = Pageable.ofSize(10);
    }

    @Nested
    @DisplayName("Create Salary Test")
    class CreateSalaryTest {

        @Test
        @DisplayName("Should create salary successfully when valid request")
        void shouldCreateSalarySuccessfullyWhenValidRequest() {
            try (var salaryMapperMock = mockStatic(SalaryMapper.class)) {
                var salaryToSave = Salary.builder()
                        .id("salary-1")
                        .amount(salaryRequest.amount())
                        .currency(salaryRequest.currency())
                        .employmentInformation(employmentInformation)
                        .build();

                when(salaryRepository.findByAmountAndCurrencyAndEmploymentInformationId(
                        eq(salaryRequest.amount()), eq(salaryRequest.currency()), eq(salaryRequest.employmentInformationId())))
                        .thenReturn(Optional.empty());
                when(employmentInformationRepository.findById(salaryRequest.employmentInformationId()))
                        .thenReturn(Optional.of(employmentInformation));
                salaryMapperMock.when(() -> SalaryMapper.toEntity(salaryRequest, employmentInformation))
                        .thenReturn(salaryToSave);
                when(salaryRepository.save(salaryToSave)).thenReturn(salary);
                salaryMapperMock.when(() -> SalaryMapper.toDto(salary)).thenReturn(salaryResponse);

                var result = salaryServiceImp.create(salaryRequest);

                assertNotNull(result);
                assertEquals(salaryResponse, result);
                assertEquals(salaryResponse.id(), result.id());
                assertEquals(salaryResponse.amount(), result.amount());
                assertEquals(salaryResponse.currency(), result.currency());

                verify(salaryRepository).findByAmountAndCurrencyAndEmploymentInformationId(
                        salaryRequest.amount(), salaryRequest.currency(), salaryRequest.employmentInformationId());
                verify(employmentInformationRepository).findById(salaryRequest.employmentInformationId());
                verify(salaryRepository).save(salaryToSave);
            }
        }

        @Test
        @DisplayName("Should throw exception when salary already exists")
        void shouldThrowExceptionWhenSalaryAlreadyExists() {
            when(salaryRepository.findByAmountAndCurrencyAndEmploymentInformationId(
                    salaryRequest.amount(), salaryRequest.currency(), salaryRequest.employmentInformationId()))
                    .thenReturn(Optional.of(salary));

            var exception = assertThrows(IllegalArgumentException.class,
                    () -> salaryServiceImp.create(salaryRequest));
            assertEquals("Salary with amount [50000.0] and currency [PHP] already exists for EmploymentInformation with id [emp-info-1]",
                    exception.getMessage());

            verify(salaryRepository).findByAmountAndCurrencyAndEmploymentInformationId(
                    salaryRequest.amount(), salaryRequest.currency(), salaryRequest.employmentInformationId());
            verifyNoMoreInteractions(salaryRepository);
            verifyNoInteractions(employmentInformationRepository);
        }

        @Test
        @DisplayName("Should throw exception when employment information not found")
        void shouldThrowExceptionWhenEmploymentInformationNotFound() {
            when(salaryRepository.findByAmountAndCurrencyAndEmploymentInformationId(
                    salaryRequest.amount(), salaryRequest.currency(), salaryRequest.employmentInformationId()))
                    .thenReturn(Optional.empty());
            when(employmentInformationRepository.findById(salaryRequest.employmentInformationId()))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(NotFoundException.class,
                    () -> salaryServiceImp.create(salaryRequest));
            assertEquals(new NotFoundException(employmentInformation.getId(), EMPLOYMENT_INFORMATION).getMessage(),
                    exception.getMessage());

            verify(salaryRepository).findByAmountAndCurrencyAndEmploymentInformationId(
                    salaryRequest.amount(), salaryRequest.currency(), salaryRequest.employmentInformationId());
            verify(employmentInformationRepository).findById(salaryRequest.employmentInformationId());
            verifyNoMoreInteractions(salaryRepository);
            verifyNoMoreInteractions(employmentInformationRepository);
        }
    }

    @Nested
    @DisplayName("Find Salary Test")
    class FindSalaryTest {

        @Test
        @DisplayName("Should find all salary paginated by 10")
        void shouldFindAllPaginatedBy10() {
            var page = new PageImpl<>(List.of(salary));
            try (var salaryMapperMock = mockStatic(SalaryMapper.class)) {
                when(salaryRepository.findAll(pageable))
                        .thenReturn(page);
                salaryMapperMock.when(() -> SalaryMapper.toDto(salary))
                        .thenReturn(salaryResponse);

                var result = salaryServiceImp.findAll(pageable);

                assertNotNull(result);
                assertEquals(1, result.getTotalElements());
                assertEquals(1, result.getTotalPages());
                assertFalse(result.isEmpty());
                assertEquals(salaryResponse, result.getContent().getFirst());
            }
        }

        @Test
        @DisplayName("Should find salary by id successfully when exists")
        void shouldFindSalaryByIdSuccessfullyWhenExists() {
            try (var salaryMapperMock = mockStatic(SalaryMapper.class)) {
                when(salaryRepository.findById(salary.getId()))
                        .thenReturn(Optional.of(salary));
                salaryMapperMock.when(() -> SalaryMapper.toDto(salary))
                        .thenReturn(salaryResponse);

                var result = salaryServiceImp.findById(salary.getId());

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(salaryResponse, result.get());
            }
        }

        @Test
        @DisplayName("Should return empty page when no salaries exist")
        void shouldReturnEmptyPageWhenNoSalariesExist() {
            var page = new PageImpl<Salary>(List.of());
            try (var salaryMapperMock = mockStatic(SalaryMapper.class)) {
                when(salaryRepository.findAll(pageable))
                        .thenReturn(page);

                var result = salaryServiceImp.findAll(pageable);

                assertNotNull(result);
                assertEquals(0, result.getTotalElements());
                assertTrue(result.isEmpty());
                assertTrue(result.getContent().isEmpty());
                salaryMapperMock.verifyNoInteractions();
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when salary not found by id")
        void shouldThrowNotFoundExceptionWhenSalaryNotFoundById() {
            when(salaryRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> salaryServiceImp.findById("non-existent-id"));

            verify(salaryRepository).findById("non-existent-id");
        }
    }

    @Nested
    @DisplayName("Update Salary Test")
    class UpdateSalaryTest {

        @Test
        @DisplayName("Should update salary successfully when valid request")
        void shouldUpdateSalarySuccessfullyWhenValidRequest() {
            var updatedSalaryRequest = SalaryRequest.builder()
                    .amount(60000.0)
                    .currency("USD")
                    .employmentInformationId("emp-info-1")
                    .build();

            var updatedSalary = Salary.builder()
                    .id("salary-1")
                    .amount(updatedSalaryRequest.amount())
                    .currency(updatedSalaryRequest.currency())
                    .employmentInformation(employmentInformation)
                    .build();

            var updatedSalaryResponse = SalaryResponse.builder()
                    .id("salary-1")
                    .amount(updatedSalaryRequest.amount())
                    .currency(updatedSalaryRequest.currency())
                    .build();

            try (var salaryMapperMock = mockStatic(SalaryMapper.class);
                 var mergeUtilMock = mockStatic(MergeUtil.class)) {
                when(salaryRepository.findById(salary.getId()))
                        .thenReturn(Optional.of(salary));
                salaryMapperMock.when(() -> SalaryMapper.toEntity(updatedSalaryRequest))
                        .thenReturn(updatedSalary);
                mergeUtilMock.when(() -> MergeUtil.merge(salary, updatedSalary))
                        .thenReturn(updatedSalary);
                when(salaryRepository.save(updatedSalary)).thenReturn(updatedSalary);
                salaryMapperMock.when(() -> SalaryMapper.toDto(updatedSalary))
                        .thenReturn(updatedSalaryResponse);

                var result = salaryServiceImp.update(salary.getId(), updatedSalaryRequest);

                assertNotNull(result);
                assertEquals(updatedSalaryResponse, result);
                assertEquals(updatedSalaryRequest.amount(), result.amount());
                assertEquals(updatedSalaryRequest.currency(), result.currency());

                verify(salaryRepository).findById(salary.getId());
                verify(salaryRepository).save(updatedSalary);
            }
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when id is null")
        void shouldThrowInvalidRequestExceptionWhenIdIsNull() {
            assertThrows(InvalidRequestException.class,
                    () -> salaryServiceImp.update(null, salaryRequest));

            verifyNoInteractions(salaryRepository);
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when id is empty")
        void shouldThrowInvalidRequestExceptionWhenIdIsEmpty() {
            assertThrows(InvalidRequestException.class,
                    () -> salaryServiceImp.update("", salaryRequest));

            verifyNoInteractions(salaryRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when salary not found")
        void shouldThrowNotFoundExceptionWhenSalaryNotFound() {
            when(salaryRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> salaryServiceImp.update("non-existent-id", salaryRequest));

            verify(salaryRepository).findById("non-existent-id");
            verifyNoMoreInteractions(salaryRepository);
        }
    }

    @Nested
    @DisplayName("Delete Salary Test")
    class DeleteSalaryTest {

        @Test
        @DisplayName("Should delete salary successfully when exists")
        void shouldDeleteSalarySuccessfullyWhenExists() {
            try (var salaryMapperMock = mockStatic(SalaryMapper.class)) {
                when(salaryRepository.findById(salary.getId()))
                        .thenReturn(Optional.of(salary));
                salaryMapperMock.when(() -> SalaryMapper.toDto(salary))
                        .thenReturn(salaryResponse);
                when(salaryRepository.existsById(salary.getId()))
                        .thenReturn(false);

                boolean result = salaryServiceImp.delete(salary.getId());

                assertTrue(result);
                verify(salaryRepository).findById(salary.getId());
                verify(salaryRepository).deleteById(salary.getId());
                verify(salaryRepository).existsById(salary.getId());
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when salary not found")
        void shouldThrowNotFoundExceptionWhenSalaryNotFound() {
            when(salaryRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> salaryServiceImp.delete("non-existent-id"));

            verify(salaryRepository).findById("non-existent-id");
            verifyNoMoreInteractions(salaryRepository);
        }

        @Test
        @DisplayName("Should return false when salary not deleted")
        void shouldReturnFalseWhenSalaryNotDeleted() {
            try (var salaryMapperMock = mockStatic(SalaryMapper.class)) {
                when(salaryRepository.findById(salary.getId()))
                        .thenReturn(Optional.of(salary));
                salaryMapperMock.when(() -> SalaryMapper.toDto(salary))
                        .thenReturn(salaryResponse);
                when(salaryRepository.existsById(salary.getId()))
                        .thenReturn(true);

                boolean result = salaryServiceImp.delete(salary.getId());

                assertFalse(result);
                verify(salaryRepository).findById(salary.getId());
                verify(salaryRepository).deleteById(salary.getId());
                verify(salaryRepository).existsById(salary.getId());
            }
        }
    }
}