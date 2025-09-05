package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.EmploymentInformationSalaryOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EmploymentInformationSalaryOverrideRepository extends JpaRepository<EmploymentInformationSalaryOverride, String> {
    Optional<EmploymentInformationSalaryOverride> findByIdAndEmploymentInformationId(String id, String employmentInformationId);

    Optional<EmploymentInformationSalaryOverride> findBySalaryAndEffectiveDateAndEmploymentInformationId(double salary, LocalDate effectiveDate, String employmentInformationId);
}
