package dev.araopj.hrplatformapi.salary.repository;

import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryGradeRepository extends JpaRepository<SalaryGrade, String> {
}
