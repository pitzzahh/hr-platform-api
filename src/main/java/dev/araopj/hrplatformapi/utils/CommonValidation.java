package dev.araopj.hrplatformapi.utils;

import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonValidation {
    private final EmploymentInformationRepository employmentInformationRepository;

    public EmploymentInformation validateEmploymentInformationExists(String firstId, String secondId, NotFoundException.EntityType entityType) {
        var optionalEmploymentInformation = employmentInformationRepository.findById(firstId);

        if (optionalEmploymentInformation.isEmpty()) {
            log.warn("Checking employment information id from request [{}] not found, falling back to path variable [{}]",
                    firstId,
                    secondId
            );
            optionalEmploymentInformation = employmentInformationRepository.findById(secondId);
            if (optionalEmploymentInformation.isEmpty()) {
                throw new NotFoundException(
                        firstId,
                        entityType
                );
            }
        }

        return optionalEmploymentInformation.get();
    }
}
