package com.task.fileuploadapp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.task.fileuploadapp.domain.FixedExtensionPolicy;
import com.task.fileuploadapp.repository.FixedExtensionPolicyRepository;
import com.task.fileuploadapp.service.exception.BadRequestException;
import com.task.fileuploadapp.service.exception.NotFoundException;

@Service
public class ExtensionPolicyService {

    private final FixedExtensionPolicyRepository fixedRepository;

    public ExtensionPolicyService(
        FixedExtensionPolicyRepository fixedRepository
    ) {
        this.fixedRepository = fixedRepository;
    }

    @Transactional(readOnly = true)
    public List<FixedExtensionPolicy> getFixedPolicies() {
        return fixedRepository.findAllByOrderByExtensionAsc();
    }

    @Transactional
    public FixedExtensionPolicy updateFixedPolicy(String rawExtension, boolean enabled) {
        String normalized = normalizeOrThrow(rawExtension);
        FixedExtensionPolicy policy = fixedRepository.findById(normalized)
                .orElseThrow(() -> new NotFoundException("Unknown fixed extension: " + normalized));
        policy.setEnabled(enabled);
        return fixedRepository.save(policy);
    }

    private String normalizeOrThrow(String rawExtension) {
        String normalized = ExtensionNormalizer.normalize(rawExtension);
        if (!ExtensionNormalizer.isValid(normalized)) {
            throw new BadRequestException("Invalid extension. Use 1-20 lowercase letters or digits only.");
        }
        return normalized;
    }
}
