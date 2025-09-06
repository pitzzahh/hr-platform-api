package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findByUserId(String userId);
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.idDocuments LEFT JOIN FETCH e.employmentInformation")
    Page<Employee> findAllWithIdDocumentsAndEmploymentInformation(Pageable pageable);
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.idDocuments")
    Page<Employee> findAllWithIdDocuments(Pageable pageable);
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.employmentInformation")
    Page<Employee> findAllWithEmploymentInformation(Pageable pageable);
    Optional<Employee> findByEmployeeNumberOrEmailOrTaxPayerIdentificationNumberOrFirstNameAndLastNameOrFirstNameAndMiddleNameAndLastName(Long employeeNumber, String email, String taxPayerIdentificationNumber, String firstName, String lastName, String firstName1, String middleName, String lastName1);
}
