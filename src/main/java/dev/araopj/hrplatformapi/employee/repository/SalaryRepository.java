package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, String> {
    Optional<Salary> findByAmountAndCurrencyAndEmploymentInformationId(double amount, String currency, String employmentInformationId);
}
