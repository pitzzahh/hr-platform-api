package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EmploymentInformationRepository extends JpaRepository<EmploymentInformation, String> {
    Page<EmploymentInformation> findByEmployeeId(String employeeId, Pageable pageable);

    Optional<EmploymentInformation> findByStartDateAndEndDateAndRemarksAndEmployeeId(LocalDate startDate, LocalDate endDate, String remarks, String employeeId);
}
