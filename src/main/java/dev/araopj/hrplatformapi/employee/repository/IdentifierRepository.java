package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.Identifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdentifierRepository extends JpaRepository<Identifier, String> {
    Optional<Identifier> findByIdentifierNumber(String s);
}
