package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.EmploymentInformationSalaryOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmploymentInformationSalaryOverrideRepository extends JpaRepository<EmploymentInformationSalaryOverride, String> {
}
