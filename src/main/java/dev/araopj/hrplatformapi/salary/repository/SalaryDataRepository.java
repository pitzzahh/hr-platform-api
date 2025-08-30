package dev.araopj.hrplatformapi.salary.repository;

import dev.araopj.hrplatformapi.salary.model.SalaryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryDataRepository extends JpaRepository<SalaryData,String> {
}
