package dev.araopj.hrplatformapi.salary.repository;

import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryGradeRepository extends JpaRepository<SalaryGrade, String> {

    Optional<SalaryGrade> findBySalaryGradeAndEffectiveDate(byte salaryGrade, LocalDate localDate);

    @Query("SELECT DISTINCT sg FROM SalaryGrade sg LEFT JOIN FETCH sg.salaryData")
    List<SalaryGrade> findAllWithSalaryData();

    @Query("SELECT sg FROM SalaryGrade sg LEFT JOIN FETCH sg.salaryData WHERE sg.id = ?1")
    Optional<SalaryGrade> findSalaryGradeWithSalaryDataById(String id);

}
