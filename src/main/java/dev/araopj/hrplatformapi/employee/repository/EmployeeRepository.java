package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findByUserId(String userId);

    Optional<Employee> findByEmployeeNumberOrEmailOrTaxPayerIdentificationNumberOrFirstNameAndLastNameOrFirstNameAndMiddleNameAndLastName(Long employeeNumber, String email, String taxPayerIdentificationNumber, String firstName, String lastName, String firstName1, String middleName, String lastName1);
}
