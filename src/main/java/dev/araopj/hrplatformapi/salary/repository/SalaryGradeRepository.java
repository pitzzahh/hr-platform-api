package dev.araopj.hrplatformapi.salary.repository;

import dev.araopj.hrplatformapi.salary.model.SalaryGrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SalaryGradeRepository extends JpaRepository<SalaryGrade, String> {

    Optional<SalaryGrade> findBySalaryGradeAndEffectiveDate(int salaryGrade, LocalDate localDate);

    @Query("SELECT DISTINCT sg FROM SalaryGrade sg LEFT JOIN FETCH sg.salaryData")
    Page<SalaryGrade> findAllWithSalaryData(Pageable pageable);

    @Query("SELECT sg FROM SalaryGrade sg LEFT JOIN FETCH sg.salaryData WHERE sg.id = ?1")
    Optional<SalaryGrade> findSalaryGradeWithSalaryDataById(String id);

}
