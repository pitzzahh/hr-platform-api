package dev.araopj.hrplatformapi.salary.repository;

import dev.araopj.hrplatformapi.salary.model.SalaryData;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryDataRepository extends JpaRepository<SalaryData, String> {
    List<SalaryData> findBySalaryGrade_Id(String salaryGradeId, Limit limit);
}
