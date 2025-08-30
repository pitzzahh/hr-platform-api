package dev.araopj.hrplatformapi.audit.repository;

import dev.araopj.hrplatformapi.audit.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<Audit, String> {
}
