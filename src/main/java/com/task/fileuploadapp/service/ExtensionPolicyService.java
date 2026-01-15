package com.task.fileuploadapp.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.task.fileuploadapp.domain.CustomBlockedExtension;
import com.task.fileuploadapp.domain.FixedExtensionPolicy;
import com.task.fileuploadapp.repository.CustomBlockedExtensionRepository;
import com.task.fileuploadapp.repository.FixedExtensionPolicyRepository;
import com.task.fileuploadapp.service.exception.BadRequestException;
import com.task.fileuploadapp.service.exception.ConflictException;
import com.task.fileuploadapp.service.exception.NotFoundException;

@Service
public class ExtensionPolicyService {

    public static final int CUSTOM_LIMIT = 200;

    private final FixedExtensionPolicyRepository fixedRepository;
    private final CustomBlockedExtensionRepository customRepository;

    public ExtensionPolicyService(
        FixedExtensionPolicyRepository fixedRepository,
        CustomBlockedExtensionRepository customRepository
    ) {
        this.fixedRepository = fixedRepository;
        this.customRepository = customRepository;
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

    @Transactional(readOnly = true)
    public List<CustomBlockedExtension> getCustomExtensions() {
        return customRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public CustomBlockedExtension addCustomExtension(String rawExtension) {
        String normalized = normalizeOrThrow(rawExtension);

        if (fixedRepository.existsById(normalized)) {
            throw new ConflictException("Extension is managed by the fixed list. Use the checkbox instead.");
        }

        List<CustomBlockedExtension> locked = customRepository.findAllForUpdate();
        if (locked.size() >= CUSTOM_LIMIT) {
            throw new ConflictException("Custom extension limit reached (" + CUSTOM_LIMIT + ").");
        }

        if (customRepository.existsByExtension(normalized)) {
            throw new ConflictException("Extension already exists.");
        }

        try {
            return customRepository.save(new CustomBlockedExtension(normalized));
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Extension already exists.");
        }
    }

    @Transactional
    public void deleteCustomExtension(Long id) {
        if (!customRepository.existsById(id)) {
            throw new NotFoundException("Custom extension not found.");
        }
        customRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean isBlocked(String rawExtension) {
        String normalized = ExtensionNormalizer.normalize(rawExtension);
        if (normalized.isEmpty()) {
            return false;
        }
        if (!ExtensionNormalizer.isValid(normalized)) {
            return true;
        }
        if (fixedRepository.findById(normalized).map(FixedExtensionPolicy::isEnabled).orElse(false)) {
            return true;
        }
        return customRepository.existsByExtension(normalized);
    }

    private String normalizeOrThrow(String rawExtension) {
        String normalized = ExtensionNormalizer.normalize(rawExtension);
        if (!ExtensionNormalizer.isValid(normalized)) {
            throw new BadRequestException("Invalid extension. Use 1-20 lowercase letters or digits only.");
        }
        return normalized;
    }
}
