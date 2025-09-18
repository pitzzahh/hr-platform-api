package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.EmployeeRequest;
import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationRequest;
import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentRequest;
import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentTypeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmployeeResponse;
import dev.araopj.hrplatformapi.employee.model.*;
import dev.araopj.hrplatformapi.employee.repository.EmployeeRepository;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.EmployeeMapper;
import dev.araopj.hrplatformapi.utils.mappers.EmploymentInformationMapper;
import dev.araopj.hrplatformapi.utils.mappers.IdDocumentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeServiceImp Test")
class EmployeeServiceImpTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeServiceImp employeeServiceImp;

    private Employee employee;
    private EmployeeResponse employeeResponse;
    private EmployeeRequest employeeRequest;
    private IdDocument idDocument;
    private EmploymentInformation employmentInformation;
    private IdDocumentRequest idDocumentRequest;
    private EmploymentInformationRequest employmentInformationRequest;
    private Pageable pageable;

    @BeforeEach
    void setup() {
        var idDocumentType = IdDocumentType.builder()
                .id("type-1")
                .code("PASSPORT")
                .name("Passport")
                .description("Government-issued passport")
                .category("Government")
                .build();

        idDocument = IdDocument.builder()
                .id("doc-1")
                .identifierNumber("123456")
                .idDocumentType(idDocumentType)
                .issuedDate(LocalDate.of(2020, 1, 1))
                .issuedPlace("Manila")
                .employee(null) // Avoid circular reference
                .build();

        employmentInformation = EmploymentInformation.builder()
                .id("emp-info-1")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 12, 31))
                .employmentStatus(EmploymentStatus.PERMANENT)
                .build();

        employee = Employee.builder()
                .id("emp-1")
                .employeeNumber("EMP001")
                .itemNumber("ITEM001")
                .firstName("John")
                .middleName("A")
                .lastName("Doe")
                .photo("photo.jpg")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .gender(Gender.MALE)
                .taxPayerIdentificationNumber("TIN123")
                .civilStatus(CivilStatus.SINGLE)
                .bankAccountNumber("BANK123")
                .archived(false)
                .userId("user-1")
                .idDocuments(Set.of(idDocument))
                .employmentInformation(Set.of(employmentInformation))
                .build();

        employeeResponse = EmployeeResponse.builder()
                .id("emp-1")
                .employeeNumber("EMP001")
                .itemNumber("ITEM001")
                .firstName("John")
                .middleName("A")
                .lastName("Doe")
                .photo("photo.jpg")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .gender(Gender.MALE)
                .taxPayerIdentificationNumber("TIN123")
                .civilStatus(CivilStatus.SINGLE)
                .bankAccountNumber("BANK123")
                .archived(false)
                .userId("user-1")
                .idDocumentResponses(Set.of())
                .employmentInformationResponses(Set.of())
                .build();

        var idDocumentTypeRequest = IdDocumentTypeRequest.builder()
                .code("PASSPORT")
                .name("Passport")
                .description("Government-issued passport")
                .category("Government")
                .identifierId("type-1")
                .build();

        idDocumentRequest = IdDocumentRequest.builder()
                .identifierNumber("123456")
                .idDocumentTypeRequest(idDocumentTypeRequest)
                .issuedDate(LocalDate.of(2020, 1, 1))
                .issuedPlace("Manila")
                .employeeId("emp-1")
                .build();

        employmentInformationRequest = EmploymentInformationRequest.builder()
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 12, 31))
                .employmentStatus(EmploymentStatus.PERMANENT)
                .employeeId("emp-1")
                .build();

        employeeRequest = EmployeeRequest.builder()
                .employeeNumber("EMP001")
                .itemNumber("ITEM001")
                .firstName("John")
                .middleName("A")
                .lastName("Doe")
                .photo("photo.jpg")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .gender(Gender.MALE)
                .taxPayerIdentificationNumber("TIN123")
                .civilStatus(CivilStatus.SINGLE)
                .bankAccountNumber("BANK123")
                .archived(false)
                .userId("user-1")
                .idDocumentRequests(Set.of(idDocumentRequest))
                .employmentInformationRequests(Set.of(employmentInformationRequest))
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("Find Employee Test")
    class FindEmployeeTest {

        @Test
        @DisplayName("Should find all employees without relations")
        void shouldFindAllEmployeesWithoutRelations() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                var employees = List.of(employee);
                var page = new PageImpl<>(employees, pageable, employees.size());
                when(employeeRepository.findAll(pageable)).thenReturn(page);
                mapperMock.when(() -> EmployeeMapper.toDto(employee, false, false))
                        .thenReturn(employeeResponse);

                var result = employeeServiceImp.findAll(pageable, false, false);

                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals(employeeResponse, result.getContent().getFirst());
                verify(employeeRepository).findAll(pageable);
            }
        }

        @Test
        @DisplayName("Should find all employees with ID documents")
        void shouldFindAllEmployeesWithIdDocuments() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                var employees = List.of(employee);
                var page = new PageImpl<>(employees, pageable, employees.size());
                when(employeeRepository.findAllWithIdDocuments(pageable)).thenReturn(page);
                mapperMock.when(() -> EmployeeMapper.toDto(employee, true, false))
                        .thenReturn(employeeResponse);

                var result = employeeServiceImp.findAll(pageable, true, false);

                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals(employeeResponse, result.getContent().getFirst());
                verify(employeeRepository).findAllWithIdDocuments(pageable);
            }
        }

        @Test
        @DisplayName("Should find all employees with employment information")
        void shouldFindAllEmployeesWithEmploymentInformation() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                var employees = List.of(employee);
                var page = new PageImpl<>(employees, pageable, employees.size());
                when(employeeRepository.findAllWithEmploymentInformation(pageable)).thenReturn(page);
                mapperMock.when(() -> EmployeeMapper.toDto(employee, false, true))
                        .thenReturn(employeeResponse);

                var result = employeeServiceImp.findAll(pageable, false, true);

                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals(employeeResponse, result.getContent().getFirst());
                verify(employeeRepository).findAllWithEmploymentInformation(pageable);
            }
        }

        @Test
        @DisplayName("Should find all employees with both ID documents and employment information")
        void shouldFindAllEmployeesWithBothRelations() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                var employees = List.of(employee);
                var page = new PageImpl<>(employees, pageable, employees.size());
                when(employeeRepository.findAllWithIdDocumentsAndEmploymentInformation(pageable)).thenReturn(page);
                mapperMock.when(() -> EmployeeMapper.toDto(employee, true, true))
                        .thenReturn(employeeResponse);

                var result = employeeServiceImp.findAll(pageable, true, true);

                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals(employeeResponse, result.getContent().getFirst());
                verify(employeeRepository).findAllWithIdDocumentsAndEmploymentInformation(pageable);
            }
        }

        @Test
        @DisplayName("Should return empty page when no employees exist")
        void shouldReturnEmptyPageWhenNoEmployeesExist() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                when(employeeRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

                var result = employeeServiceImp.findAll(pageable, false, false);

                assertNotNull(result);
                assertTrue(result.getContent().isEmpty());
                verify(employeeRepository).findAll(pageable);
                mapperMock.verifyNoInteractions();
            }
        }

        @Test
        @DisplayName("Should find employee by ID successfully when exists")
        void shouldFindEmployeeByIdSuccessfullyWhenExists() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
                mapperMock.when(() -> EmployeeMapper.toDto(employee, false, false))
                        .thenReturn(employeeResponse);

                var result = employeeServiceImp.findById(employee.getId(), false, false);

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(employeeResponse, result.get());
                verify(employeeRepository).findById(employee.getId());
            }
        }

        @Test
        @DisplayName("Should find employee by ID with ID documents")
        void shouldFindEmployeeByIdWithIdDocuments() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
                mapperMock.when(() -> EmployeeMapper.toDto(employee, true, false))
                        .thenReturn(employeeResponse);

                var result = employeeServiceImp.findById(employee.getId(), true, false);

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(employeeResponse, result.get());
                verify(employeeRepository).findById(employee.getId());
            }
        }

        @Test
        @DisplayName("Should find employee by ID with employment information")
        void shouldFindEmployeeByIdWithEmploymentInformation() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
                mapperMock.when(() -> EmployeeMapper.toDto(employee, false, true))
                        .thenReturn(employeeResponse);

                var result = employeeServiceImp.findById(employee.getId(), false, true);

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(employeeResponse, result.get());
                verify(employeeRepository).findById(employee.getId());
            }
        }

        @Test
        @DisplayName("Should find employee by ID with both relations")
        void shouldFindEmployeeByIdWithBothRelations() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
                mapperMock.when(() -> EmployeeMapper.toDto(employee, true, true))
                        .thenReturn(employeeResponse);

                var result = employeeServiceImp.findById(employee.getId(), true, true);

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(employeeResponse, result.get());
                verify(employeeRepository).findById(employee.getId());
            }
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when ID is null")
        void shouldThrowInvalidRequestExceptionWhenIdIsNull() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> employeeServiceImp.findById(null, false, false));
            assertEquals("Employee ID must be provided as path", exception.getMessage());

            verifyNoInteractions(employeeRepository);
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when ID is empty")
        void shouldThrowInvalidRequestExceptionWhenIdIsEmpty() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> employeeServiceImp.findById("", false, false));
            assertEquals("Employee ID must be provided as path", exception.getMessage());

            verifyNoInteractions(employeeRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when employee not found by ID")
        void shouldThrowNotFoundExceptionWhenEmployeeNotFoundById() {
            when(employeeRepository.findById("non-existent-id")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> employeeServiceImp.findById("non-existent-id", false, false));

            verify(employeeRepository).findById("non-existent-id");
        }

        @Test
        @DisplayName("Should find employee by user ID successfully when exists")
        void shouldFindEmployeeByUserIdSuccessfullyWhenExists() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                when(employeeRepository.findByUserId(employee.getUserId())).thenReturn(Optional.of(employee));
                mapperMock.when(() -> EmployeeMapper.toDto(employee, false, false))
                        .thenReturn(employeeResponse);

                var result = employeeServiceImp.findByUserId(employee.getUserId(), false, false);

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(employeeResponse, result.get());
                verify(employeeRepository).findByUserId(employee.getUserId());
            }
        }

        @Test
        @DisplayName("Should find employee by user ID with ID documents")
        void shouldFindEmployeeByUserIdWithIdDocuments() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                when(employeeRepository.findByUserId(employee.getUserId())).thenReturn(Optional.of(employee));
                mapperMock.when(() -> EmployeeMapper.toDto(employee, true, false))
                        .thenReturn(employeeResponse);

                var result = employeeServiceImp.findByUserId(employee.getUserId(), true, false);

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(employeeResponse, result.get());
                verify(employeeRepository).findByUserId(employee.getUserId());
            }
        }

        @Test
        @DisplayName("Should find employee by user ID with employment information")
        void shouldFindEmployeeByUserIdWithEmploymentInformation() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                when(employeeRepository.findByUserId(employee.getUserId())).thenReturn(Optional.of(employee));
                mapperMock.when(() -> EmployeeMapper.toDto(employee, false, true))
                        .thenReturn(employeeResponse);

                var result = employeeServiceImp.findByUserId(employee.getUserId(), false, true);

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(employeeResponse, result.get());
                verify(employeeRepository).findByUserId(employee.getUserId());
            }
        }

        @Test
        @DisplayName("Should find employee by user ID with both relations")
        void shouldFindEmployeeByUserIdWithBothRelations() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                when(employeeRepository.findByUserId(employee.getUserId())).thenReturn(Optional.of(employee));
                mapperMock.when(() -> EmployeeMapper.toDto(employee, true, true))
                        .thenReturn(employeeResponse);

                var result = employeeServiceImp.findByUserId(employee.getUserId(), true, true);

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(employeeResponse, result.get());
                verify(employeeRepository).findByUserId(employee.getUserId());
            }
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when user ID is null")
        void shouldThrowInvalidRequestExceptionWhenUserIdIsNull() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> employeeServiceImp.findByUserId(null, false, false));
            assertEquals("User ID must be provided as path", exception.getMessage());

            verifyNoInteractions(employeeRepository);
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when user ID is empty")
        void shouldThrowInvalidRequestExceptionWhenUserIdIsEmpty() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> employeeServiceImp.findByUserId("", false, false));
            assertEquals("User ID must be provided as path", exception.getMessage());

            verifyNoInteractions(employeeRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when employee not found by user ID")
        void shouldThrowNotFoundExceptionWhenEmployeeNotFoundByUserId() {
            when(employeeRepository.findByUserId("non-existent-user-id")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> employeeServiceImp.findByUserId("non-existent-user-id", false, false));

            verify(employeeRepository).findByUserId("non-existent-user-id");
        }
    }

    @Nested
    @DisplayName("Create Employee Test")
    class CreateEmployeeTest {

        @Test
        @DisplayName("Should create employees successfully when valid requests")
        void shouldCreateEmployeesSuccessfullyWhenValidRequests() {
            try (var mapperMock = mockStatic(EmployeeMapper.class);
                 var idDocumentMapperMock = mockStatic(IdDocumentMapper.class);
                 var employmentInformationMapperMock = mockStatic(EmploymentInformationMapper.class)) {
                var employeeRequests = List.of(employeeRequest);
                when(employeeRepository.findByEmployeeNumberOrEmailOrTaxPayerIdentificationNumberOrFirstNameAndLastNameOrFirstNameAndMiddleNameAndLastName(
                        eq(employeeRequest.employeeNumber()),
                        eq(employeeRequest.email()),
                        eq(employeeRequest.taxPayerIdentificationNumber()),
                        eq(employeeRequest.firstName()),
                        eq(employeeRequest.lastName()),
                        eq(employeeRequest.firstName()),
                        eq(employeeRequest.middleName()),
                        eq(employeeRequest.lastName())))
                        .thenReturn(Optional.empty());
                idDocumentMapperMock.when(() -> IdDocumentMapper.toEntity(idDocumentRequest))
                        .thenReturn(idDocument);
                employmentInformationMapperMock.when(() -> EmploymentInformationMapper.toEntity(employmentInformationRequest))
                        .thenReturn(employmentInformation);
                mapperMock.when(() -> EmployeeMapper.toEntity(eq(employeeRequest), anySet(), anySet()))
                        .thenReturn(employee);
                when(employeeRepository.saveAll(anyList())).thenReturn(List.of(employee));
                mapperMock.when(() -> EmployeeMapper.toDto(employee, false, false))
                        .thenReturn(employeeResponse);

                var result = employeeServiceImp.create(employeeRequests);

                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals(employeeResponse, result.getFirst());
                verify(employeeRepository).findByEmployeeNumberOrEmailOrTaxPayerIdentificationNumberOrFirstNameAndLastNameOrFirstNameAndMiddleNameAndLastName(
                        employeeRequest.employeeNumber(),
                        employeeRequest.email(),
                        employeeRequest.taxPayerIdentificationNumber(),
                        employeeRequest.firstName(),
                        employeeRequest.lastName(),
                        employeeRequest.firstName(),
                        employeeRequest.middleName(),
                        employeeRequest.lastName());
                verify(employeeRepository).saveAll(anyList());
            }
        }

        @Test
        @DisplayName("Should create employees successfully with null relations")
        void shouldCreateEmployeesSuccessfullyWithNullRelations() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                var employeeRequests = List.of(employeeRequest);
                var employeeRequestNoRelations = EmployeeRequest.builder()
                        .employeeNumber("EMP001")
                        .itemNumber("ITEM001")
                        .firstName("John")
                        .middleName("A")
                        .lastName("Doe")
                        .photo("photo.jpg")
                        .dateOfBirth(LocalDate.of(1990, 1, 1))
                        .email("john.doe@example.com")
                        .phoneNumber("1234567890")
                        .gender(Gender.MALE)
                        .taxPayerIdentificationNumber("TIN123")
                        .civilStatus(CivilStatus.SINGLE)
                        .bankAccountNumber("BANK123")
                        .archived(false)
                        .userId("user-1")
                        .idDocumentRequests(null)
                        .employmentInformationRequests(null)
                        .build();
                var employeeNoRelations = Employee.builder()
                        .id("emp-1")
                        .employeeNumber("EMP001")
                        .itemNumber("ITEM001")
                        .firstName("John")
                        .middleName("A")
                        .lastName("Doe")
                        .photo("photo.jpg")
                        .dateOfBirth(LocalDate.of(1990, 1, 1))
                        .email("john.doe@example.com")
                        .phoneNumber("1234567890")
                        .gender(Gender.MALE)
                        .taxPayerIdentificationNumber("TIN123")
                        .civilStatus(CivilStatus.SINGLE)
                        .bankAccountNumber("BANK123")
                        .archived(false)
                        .userId("user-1")
                        .idDocuments(Set.of())
                        .employmentInformation(Set.of())
                        .build();
                when(employeeRepository.findByEmployeeNumberOrEmailOrTaxPayerIdentificationNumberOrFirstNameAndLastNameOrFirstNameAndMiddleNameAndLastName(
                        eq(employeeRequestNoRelations.employeeNumber()),
                        eq(employeeRequestNoRelations.email()),
                        eq(employeeRequestNoRelations.taxPayerIdentificationNumber()),
                        eq(employeeRequestNoRelations.firstName()),
                        eq(employeeRequestNoRelations.lastName()),
                        eq(employeeRequestNoRelations.firstName()),
                        eq(employeeRequestNoRelations.middleName()),
                        eq(employeeRequestNoRelations.lastName())))
                        .thenReturn(Optional.empty());
                mapperMock.when(() -> EmployeeMapper.toEntity(eq(employeeRequestNoRelations), isNull(), isNull()))
                        .thenReturn(employeeNoRelations);
                when(employeeRepository.saveAll(anyList())).thenReturn(List.of(employeeNoRelations));
                mapperMock.when(() -> EmployeeMapper.toDto(employeeNoRelations, false, false))
                        .thenReturn(employeeResponse);

                var result = employeeServiceImp.create(employeeRequests);

                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals(employeeResponse, result.getFirst());
                verify(employeeRepository).findByEmployeeNumberOrEmailOrTaxPayerIdentificationNumberOrFirstNameAndLastNameOrFirstNameAndMiddleNameAndLastName(
                        employeeRequestNoRelations.employeeNumber(),
                        employeeRequestNoRelations.email(),
                        employeeRequestNoRelations.taxPayerIdentificationNumber(),
                        employeeRequestNoRelations.firstName(),
                        employeeRequestNoRelations.lastName(),
                        employeeRequestNoRelations.firstName(),
                        employeeRequestNoRelations.middleName(),
                        employeeRequestNoRelations.lastName());
                verify(employeeRepository).saveAll(anyList());
            }
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when employee already exists")
        void shouldThrowInvalidRequestExceptionWhenEmployeeAlreadyExists() {
            var employeeRequests = List.of(employeeRequest);
            when(employeeRepository.findByEmployeeNumberOrEmailOrTaxPayerIdentificationNumberOrFirstNameAndLastNameOrFirstNameAndMiddleNameAndLastName(
                    eq(employeeRequest.employeeNumber()),
                    eq(employeeRequest.email()),
                    eq(employeeRequest.taxPayerIdentificationNumber()),
                    eq(employeeRequest.firstName()),
                    eq(employeeRequest.lastName()),
                    eq(employeeRequest.firstName()),
                    eq(employeeRequest.middleName()),
                    eq(employeeRequest.lastName())))
                    .thenReturn(Optional.of(employee));

            var exception = assertThrows(InvalidRequestException.class,
                    () -> employeeServiceImp.create(employeeRequests));
            assertEquals("Employee with employee number [EMP001] or email [john.doe@example.com] or tax payer identification number [TIN123] or name [John A Doe] already exists",
                    exception.getMessage());

            verify(employeeRepository).findByEmployeeNumberOrEmailOrTaxPayerIdentificationNumberOrFirstNameAndLastNameOrFirstNameAndMiddleNameAndLastName(
                    employeeRequest.employeeNumber(),
                    employeeRequest.email(),
                    employeeRequest.taxPayerIdentificationNumber(),
                    employeeRequest.firstName(),
                    employeeRequest.lastName(),
                    employeeRequest.firstName(),
                    employeeRequest.middleName(),
                    employeeRequest.lastName());
            verifyNoMoreInteractions(employeeRepository);
        }
    }

    @Nested
    @DisplayName("Update Employee Test")
    class UpdateEmployeeTest {

        @Test
        @DisplayName("Should update employee successfully when valid request")
        void shouldUpdateEmployeeSuccessfullyWhenValidRequest() {
            try (var mapperMock = mockStatic(EmployeeMapper.class);
                 var idDocumentMapperMock = mockStatic(IdDocumentMapper.class);
                 var employmentInformationMapperMock = mockStatic(EmploymentInformationMapper.class);
                 var mergeUtilMock = mockStatic(MergeUtil.class)) {
                var updatedRequest = EmployeeRequest.builder()
                        .employeeNumber("EMP002")
                        .itemNumber("ITEM002")
                        .firstName("Jane")
                        .middleName("B")
                        .lastName("Smith")
                        .photo("photo2.jpg")
                        .dateOfBirth(LocalDate.of(1991, 2, 2))
                        .email("jane.smith@example.com")
                        .phoneNumber("0987654321")
                        .gender(Gender.FEMALE)
                        .taxPayerIdentificationNumber("TIN456")
                        .civilStatus(CivilStatus.MARRIED)
                        .bankAccountNumber("BANK456")
                        .archived(true)
                        .userId("user-2")
                        .idDocumentRequests(Set.of(idDocumentRequest))
                        .employmentInformationRequests(Set.of(employmentInformationRequest))
                        .build();
                var updatedEmployee = Employee.builder()
                        .id("emp-1")
                        .employeeNumber("EMP002")
                        .itemNumber("ITEM002")
                        .firstName("Jane")
                        .middleName("B")
                        .lastName("Smith")
                        .photo("photo2.jpg")
                        .dateOfBirth(LocalDate.of(1991, 2, 2))
                        .email("jane.smith@example.com")
                        .phoneNumber("0987654321")
                        .gender(Gender.FEMALE)
                        .taxPayerIdentificationNumber("TIN456")
                        .civilStatus(CivilStatus.MARRIED)
                        .bankAccountNumber("BANK456")
                        .archived(true)
                        .userId("user-2")
                        .idDocuments(Set.of(idDocument))
                        .employmentInformation(Set.of(employmentInformation))
                        .build();
                var updatedResponse = EmployeeResponse.builder()
                        .id("emp-1")
                        .employeeNumber("EMP002")
                        .itemNumber("ITEM002")
                        .firstName("Jane")
                        .middleName("B")
                        .lastName("Smith")
                        .photo("photo2.jpg")
                        .dateOfBirth(LocalDate.of(1991, 2, 2))
                        .email("jane.smith@example.com")
                        .phoneNumber("0987654321")
                        .gender(Gender.FEMALE)
                        .taxPayerIdentificationNumber("TIN456")
                        .civilStatus(CivilStatus.MARRIED)
                        .bankAccountNumber("BANK456")
                        .archived(true)
                        .userId("user-2")
                        .idDocumentResponses(Set.of())
                        .employmentInformationResponses(Set.of())
                        .build();

                when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
                idDocumentMapperMock.when(() -> IdDocumentMapper.toEntity(idDocumentRequest))
                        .thenReturn(idDocument);
                employmentInformationMapperMock.when(() -> EmploymentInformationMapper.toEntity(employmentInformationRequest))
                        .thenReturn(employmentInformation);
                mapperMock.when(() -> EmployeeMapper.toEntity(eq(updatedRequest), anySet(), anySet()))
                        .thenReturn(updatedEmployee);
                mergeUtilMock.when(() -> MergeUtil.merge(employee, updatedEmployee))
                        .thenReturn(updatedEmployee);
                when(employeeRepository.save(updatedEmployee)).thenReturn(updatedEmployee);
                mapperMock.when(() -> EmployeeMapper.toDto(updatedEmployee, false, false))
                        .thenReturn(updatedResponse);

                var result = employeeServiceImp.update(employee.getId(), updatedRequest);

                assertNotNull(result);
                assertEquals(updatedResponse, result);
                verify(employeeRepository).findById(employee.getId());
                verify(employeeRepository).save(updatedEmployee);
            }
        }

        @Test
        @DisplayName("Should update employee successfully with null relations")
        void shouldUpdateEmployeeSuccessfullyWithNullRelations() {
            try (var mapperMock = mockStatic(EmployeeMapper.class);
                 var mergeUtilMock = mockStatic(MergeUtil.class)) {
                var updatedRequest = EmployeeRequest.builder()
                        .employeeNumber("EMP002")
                        .itemNumber("ITEM002")
                        .firstName("Jane")
                        .middleName("B")
                        .lastName("Smith")
                        .photo("photo2.jpg")
                        .dateOfBirth(LocalDate.of(1991, 2, 2))
                        .email("jane.smith@example.com")
                        .phoneNumber("0987654321")
                        .gender(Gender.FEMALE)
                        .taxPayerIdentificationNumber("TIN456")
                        .civilStatus(CivilStatus.MARRIED)
                        .bankAccountNumber("BANK456")
                        .archived(true)
                        .userId("user-2")
                        .idDocumentRequests(null)
                        .employmentInformationRequests(null)
                        .build();
                var updatedEmployee = Employee.builder()
                        .id("emp-1")
                        .employeeNumber("EMP002")
                        .itemNumber("ITEM002")
                        .firstName("Jane")
                        .middleName("B")
                        .lastName("Smith")
                        .photo("photo2.jpg")
                        .dateOfBirth(LocalDate.of(1991, 2, 2))
                        .email("jane.smith@example.com")
                        .phoneNumber("0987654321")
                        .gender(Gender.FEMALE)
                        .taxPayerIdentificationNumber("TIN456")
                        .civilStatus(CivilStatus.MARRIED)
                        .bankAccountNumber("BANK456")
                        .archived(true)
                        .userId("user-2")
                        .idDocuments(Set.of())
                        .employmentInformation(Set.of())
                        .build();
                var updatedResponse = EmployeeResponse.builder()
                        .id("emp-1")
                        .employeeNumber("EMP002")
                        .itemNumber("ITEM002")
                        .firstName("Jane")
                        .middleName("B")
                        .lastName("Smith")
                        .photo("photo2.jpg")
                        .dateOfBirth(LocalDate.of(1991, 2, 2))
                        .email("jane.smith@example.com")
                        .phoneNumber("0987654321")
                        .gender(Gender.FEMALE)
                        .taxPayerIdentificationNumber("TIN456")
                        .civilStatus(CivilStatus.MARRIED)
                        .bankAccountNumber("BANK456")
                        .archived(true)
                        .userId("user-2")
                        .idDocumentResponses(null)
                        .employmentInformationResponses(null)
                        .build();

                when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
                mapperMock.when(() -> EmployeeMapper.toEntity(eq(updatedRequest), isNull(), isNull()))
                        .thenReturn(updatedEmployee);
                mergeUtilMock.when(() -> MergeUtil.merge(employee, updatedEmployee))
                        .thenReturn(updatedEmployee);
                when(employeeRepository.save(updatedEmployee)).thenReturn(updatedEmployee);
                mapperMock.when(() -> EmployeeMapper.toDto(updatedEmployee, false, false))
                        .thenReturn(updatedResponse);

                var result = employeeServiceImp.update(employee.getId(), updatedRequest);

                assertNotNull(result);
                assertEquals(updatedResponse, result);
                verify(employeeRepository).findById(employee.getId());
                verify(employeeRepository).save(updatedEmployee);
            }
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when ID is null")
        void shouldThrowInvalidRequestExceptionWhenIdIsNull() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> employeeServiceImp.update(null, employeeRequest));
            assertEquals("Employee ID must be provided as path", exception.getMessage());

            verifyNoInteractions(employeeRepository);
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when ID is empty")
        void shouldThrowInvalidRequestExceptionWhenIdIsEmpty() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> employeeServiceImp.update("", employeeRequest));
            assertEquals("Employee ID must be provided as path", exception.getMessage());

            verifyNoInteractions(employeeRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when employee not found")
        void shouldThrowNotFoundExceptionWhenEmployeeNotFound() {
            when(employeeRepository.findById("non-existent-id")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> employeeServiceImp.update("non-existent-id", employeeRequest));

            verify(employeeRepository).findById("non-existent-id");
            verifyNoMoreInteractions(employeeRepository);
        }
    }

    @Nested
    @DisplayName("Delete Employee Test")
    class DeleteEmployeeTest {

        @Test
        @DisplayName("Should delete employee successfully when exists")
        void shouldDeleteEmployeeSuccessfullyWhenExists() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
                mapperMock.when(() -> EmployeeMapper.toDto(employee, false, false))
                        .thenReturn(employeeResponse);
                when(employeeRepository.existsById(employee.getId())).thenReturn(false);

                var result = employeeServiceImp.delete(employee.getId());

                assertTrue(result);
                verify(employeeRepository).findById(employee.getId());
                verify(employeeRepository).deleteById(employee.getId());
                verify(employeeRepository).existsById(employee.getId());
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when employee not found")
        void shouldThrowNotFoundExceptionWhenEmployeeNotFound() {
            when(employeeRepository.findById("non-existent-id")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> employeeServiceImp.delete("non-existent-id"));

            verify(employeeRepository).findById("non-existent-id");
            verifyNoMoreInteractions(employeeRepository);
        }

        @Test
        @DisplayName("Should return false when employee not deleted")
        void shouldReturnFalseWhenEmployeeNotDeleted() {
            try (var mapperMock = mockStatic(EmployeeMapper.class)) {
                when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
                mapperMock.when(() -> EmployeeMapper.toDto(employee, false, false))
                        .thenReturn(employeeResponse);
                when(employeeRepository.existsById(employee.getId())).thenReturn(true);

                var result = employeeServiceImp.delete(employee.getId());

                assertFalse(result);
                verify(employeeRepository).findById(employee.getId());
                verify(employeeRepository).deleteById(employee.getId());
                verify(employeeRepository).existsById(employee.getId());
            }
        }
    }
}