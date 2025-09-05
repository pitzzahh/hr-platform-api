package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkplaceRepository extends JpaRepository<Workplace, String> {
    Optional<Workplace> findByCodeAndNameAndEmploymentInformationId(String code, String name, String employmentInformationId);
}
