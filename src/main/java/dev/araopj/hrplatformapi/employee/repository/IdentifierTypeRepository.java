package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.IdentifierType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentifierTypeRepository extends JpaRepository<IdentifierType, String> {
}
