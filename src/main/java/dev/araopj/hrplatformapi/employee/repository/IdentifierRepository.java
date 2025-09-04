package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.Identifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdentifierRepository extends JpaRepository<Identifier, String> {
    Optional<Identifier> findByIdentifierNumber(String s);
}
