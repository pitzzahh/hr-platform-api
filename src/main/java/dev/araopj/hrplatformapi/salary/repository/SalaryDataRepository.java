package dev.araopj.hrplatformapi.salary.repository;

import dev.araopj.hrplatformapi.salary.model.SalaryData;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryDataRepository extends JpaRepository<SalaryData, String> {
    List<SalaryData> findBySalaryGrade_Id(String salaryGradeId, Limit limit);

    Optional<SalaryData> findByIdAndSalaryGrade_Id(String id, String salaryGradeId);

    @Query("SELECT sd FROM SalaryData sd JOIN FETCH sd.salaryGrade")
    List<SalaryData> findAllWithParent();

    @Query("SELECT sd FROM SalaryData sd JOIN FETCH sd.salaryGrade WHERE sd.id = :id")
    Optional<SalaryData> findByIdWithSalaryGrade(@Param("id") String id);
}
