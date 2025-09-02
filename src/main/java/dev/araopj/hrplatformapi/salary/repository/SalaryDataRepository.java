package dev.araopj.hrplatformapi.salary.repository;

import dev.araopj.hrplatformapi.salary.model.SalaryData;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@NonNullApi
@Repository
public interface SalaryDataRepository extends JpaRepository<SalaryData, String> {

    Optional<SalaryData> findByStepAndAmountAndSalaryGrade_Id(int step, double amount, String id);

    @EntityGraph(attributePaths = "salaryGrade")
    List<SalaryData> findBySalaryGradeId(String salaryGradeId, Pageable pageable);

    @EntityGraph(attributePaths = "salaryGrade")
    @Override
    Page<SalaryData> findAll(Pageable pageable);

    Optional<SalaryData> findByIdAndSalaryGradeId(String id, String salaryGradeId);

    @Query("SELECT sd FROM SalaryData sd JOIN FETCH sd.salaryGrade")
    List<SalaryData> findAllWithParent(Pageable pageable);
}
