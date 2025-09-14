package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.SalaryRequest;
import dev.araopj.hrplatformapi.employee.dto.response.SalaryResponse;
import dev.araopj.hrplatformapi.employee.model.*;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.SalaryRepository;
import dev.araopj.hrplatformapi.exception.NotFoundException;
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
    private Employee employee;
    private Position position;
    private Workplace workplace;

    private Salary salary;
    private SalaryResponse salaryResponse;
    private SalaryRequest salaryRequest;
    private Pageable pageable;

    @BeforeEach
    void setup() {
        employee = Employee.builder()
                .id("emp-1")
                .firstName("John")
                .lastName("Doe")
                .email("john@doe.com")
                .build();

        position = Position.builder()
                .id("position-1")
                .code("ITO")
                .description("IT Officer")
                .build();

        workplace = Workplace.builder()
                .id("workplace-1")
                .name("Head Office")
                .code("HO")
                .shortName("Head Office")
                .build();

        salary = Salary
                .builder()
                .id("salary-1")
                .amount(50000.0)
                .currency("PHP")
                .build();

        salaryResponse = SalaryResponse
                .builder()
                .id("salary-1")
                .amount(50000.0)
                .currency("PHP")
                .build();

        salaryRequest = SalaryRequest
                .builder()
                .amount(50000.0)
                .currency("PHP")
                .employmentInformationId("emp-info-1")
                .build();

        employmentInformation = EmploymentInformation
                .builder()
                .id("emp-info-1")
                .employee(employee)
                .position(position)
                .workplace(workplace)
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
                when(salaryRepository.findByAmountAndCurrencyAndEmploymentInformationId(
                        salaryRequest.amount(), salaryRequest.currency(), salaryRequest.employmentInformationId()))
                        .thenReturn(Optional.empty());
                when(employmentInformationRepository.findById(salaryRequest.employmentInformationId()))
                        .thenReturn(Optional.of(employmentInformation));
                salaryMapperMock.when(() -> SalaryMapper.toEntity(salaryRequest, employmentInformation))
                        .thenReturn(salary);
                when(salaryRepository.save(salary)).thenReturn(salary);
                salaryMapperMock.when(() -> SalaryMapper.toDto(salary)).thenReturn(salaryResponse);

                var result = salaryServiceImp.create(salaryRequest);

                assertNotNull(result);
                assertEquals(result, salaryResponse);
                assertEquals(salaryResponse.id(), result.id());
                assertEquals(salaryResponse.amount(), result.amount());
                assertEquals(salaryResponse.currency(), result.currency());

                verify(salaryRepository).findByAmountAndCurrencyAndEmploymentInformationId(
                        salaryRequest.amount(), salaryRequest.currency(), salaryRequest.employmentInformationId());
                verify(employmentInformationRepository).findById(salaryRequest.employmentInformationId());
                verify(salaryRepository).save(salary);
            }
        }

        @Test
        @DisplayName("Should throw exception when salary already exists")
        void shouldThrowExceptionWhenSalaryAlreadyExists() {
            when(salaryRepository.findByAmountAndCurrencyAndEmploymentInformationId(
                    salaryRequest.amount(), salaryRequest.currency(), salaryRequest.employmentInformationId()))
                    .thenReturn(Optional.of(salary));

            try {
                salaryServiceImp.create(salaryRequest);
            } catch (IllegalArgumentException e) {
                assertEquals("Salary with amount [50000.0] and currency [PHP] already exists for EmploymentInformation with id [emp-info-1]", e.getMessage());
            }

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

            try {
                salaryServiceImp.create(salaryRequest);
            } catch (Exception e) {
                var notFoundException = new NotFoundException(employmentInformation.getId(), NotFoundException.EntityType.EMPLOYMENT_INFORMATION);
                assertEquals(notFoundException.getMessage(), e.getMessage());
                assertEquals(e, notFoundException);
            }

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
                assertTrue(result.getTotalPages() > 0);
                assertFalse(result.isEmpty());
                assertEquals(result.getContent().getFirst(), salaryResponse);

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
                assertEquals(result.get(), salaryResponse);
            }
        }

        @Test
        @DisplayName("Should return empty data when no salary exists")
        void shouldReturnEmptyPageWhenNoSalariesExist() {

            try (var salaryMapperMock = mockStatic(SalaryMapper.class)) {
                var page = new PageImpl<Salary>(List.of());

                when(salaryRepository.findAll(pageable))
                        .thenReturn(page);

                var result = salaryServiceImp.findAll(pageable);

                assertNotNull(result);
                assertEquals(0, result.getTotalElements());
                assertTrue(result.isEmpty());
                assertTrue(page.getContent().isEmpty());
                salaryMapperMock.verifyNoInteractions();
            }

        }
    }

}