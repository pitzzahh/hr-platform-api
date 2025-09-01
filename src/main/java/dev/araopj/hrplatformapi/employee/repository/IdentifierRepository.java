package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.Identifier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentifierRepository extends JpaRepository<Identifier, Long> {
}
