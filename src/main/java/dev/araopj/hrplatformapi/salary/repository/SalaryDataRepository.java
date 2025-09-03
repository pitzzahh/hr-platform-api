package dev.araopj.hrplatformapi.salary.repository;

import dev.araopj.hrplatformapi.salary.model.SalaryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryDataRepository extends JpaRepository<SalaryData, String> {

    Optional<SalaryData> findByStepAndAmountAndSalaryGradeId(int step, double amount, String id);

    Optional<SalaryData> findByIdAndSalaryGradeId(String id, String salaryGradeId);

}
