package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.Gsis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GsisRepository extends JpaRepository<Gsis, String> {
    Optional<Gsis> findByIdAndEmployee_Id(String id, String employeeId);
}
