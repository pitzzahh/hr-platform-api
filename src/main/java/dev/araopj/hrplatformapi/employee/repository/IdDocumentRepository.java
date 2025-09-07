package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.IdDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdDocumentRepository extends JpaRepository<IdDocument, String> {
    Optional<IdDocument> findByIdentifierNumber(String s);
}
