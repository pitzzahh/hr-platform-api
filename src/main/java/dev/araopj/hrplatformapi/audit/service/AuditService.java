package dev.araopj.hrplatformapi.audit.service;

import dev.araopj.hrplatformapi.audit.dto.AuditDto;
import dev.araopj.hrplatformapi.audit.model.Audit;
import dev.araopj.hrplatformapi.audit.repository.AuditRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuditService {
    private final AuditRepository auditRepository;

    public List<Audit> findAll() {
        return auditRepository.findAll();
    }

    public Optional<Audit> findById(@NotNull String id) {
        return auditRepository.findById(id);
    }

    public Audit create(@NotNull AuditDto request) {
        var no_changes_when_no_old_data = request.getOldData() == null && request.getNewData() != null;
        var no_changes_when_no_new_data = request.getOldData() != null && request.getNewData() == null;
        if (no_changes_when_no_old_data || no_changes_when_no_new_data) {
            log.warn("Creating audit with no changes: {}", request);
        }
        return auditRepository.saveAndFlush(Audit.builder()
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .action(request.getAction())
                .performedBy(request.getPerformedBy())
                .oldData(request.getOldData())
                .newData(request.getNewData())
                .changes(request.getChanges())
                .build());

    }

    public Optional<Audit> update(@NotNull String id, AuditDto request) {
        return auditRepository.findById(id)
                .map(audit -> {
                    audit.setEntityType(request.getEntityType());
                    audit.setAction(request.getAction());
                    audit.setEntityId(request.getEntityId());
                    audit.setOldData(request.getOldData());
                    audit.setNewData(request.getNewData());
                    audit.setChanges(request.getChanges());
                    audit.setPerformedBy(request.getPerformedBy());
                    return auditRepository.save(audit);
                });
    }

    public boolean delete(@NotNull String id) {
        if (!auditRepository.existsById(id)) {
            return false;
        }
        auditRepository.deleteById(id);
        return true;
    }
}
