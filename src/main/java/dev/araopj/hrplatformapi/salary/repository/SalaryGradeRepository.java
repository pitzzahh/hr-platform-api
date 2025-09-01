package dev.araopj.hrplatformapi.salary.repository;

import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryGradeRepository extends JpaRepository<SalaryGrade, String> {
    @Query("SELECT sg FROM SalaryGrade sg LEFT JOIN FETCH sg.salaryData WHERE sg.id = :id")
    Optional<SalaryGrade> findByIdWithSalaryData(@Param("id") String id);
}
