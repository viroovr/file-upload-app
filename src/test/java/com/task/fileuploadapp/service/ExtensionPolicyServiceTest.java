package com.task.fileuploadapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.task.fileuploadapp.domain.CustomBlockedExtension;
import com.task.fileuploadapp.domain.FixedExtensionPolicy;
import com.task.fileuploadapp.repository.CustomBlockedExtensionRepository;
import com.task.fileuploadapp.repository.FixedExtensionPolicyRepository;
import com.task.fileuploadapp.service.exception.BadRequestException;
import com.task.fileuploadapp.service.exception.ConflictException;
import com.task.fileuploadapp.service.exception.NotFoundException;

@DataJpaTest
@Import(ExtensionPolicyService.class)
class ExtensionPolicyServiceTest {

    @Autowired
    private ExtensionPolicyService service;

    @Autowired
    private FixedExtensionPolicyRepository fixedRepository;

    @Autowired
    private CustomBlockedExtensionRepository customRepository;

    @BeforeEach
    void setUp() {
        customRepository.deleteAll();
        fixedRepository.deleteAll();
    }

    @Test
    void addCustomExtension_normalizesAndSaves() {
        CustomBlockedExtension saved = service.addCustomExtension(".Sh");

        assertEquals("sh", saved.getExtension());
        assertTrue(customRepository.existsByExtension("sh"));
    }

    @Test
    void addCustomExtension_rejectsInvalidExtension() {
        assertThrows(BadRequestException.class, () -> service.addCustomExtension("sh!"));
        assertThrows(BadRequestException.class, () -> service.addCustomExtension("a".repeat(21)));
    }

    @Test
    void addCustomExtension_rejectsFixedOverlap() {
        fixedRepository.save(new FixedExtensionPolicy("exe", false));

        assertThrows(ConflictException.class, () -> service.addCustomExtension("exe"));
    }

    @Test
    void addCustomExtension_rejectsDuplicate() {
        service.addCustomExtension("zip");

        assertThrows(ConflictException.class, () -> service.addCustomExtension("zip"));
    }

    @Test
    void addCustomExtension_rejectsWhenLimitReached() {
        for (int i = 0; i < ExtensionPolicyService.CUSTOM_LIMIT; i++) {
            customRepository.save(new CustomBlockedExtension("e" + i));
        }

        assertThrows(ConflictException.class, () -> service.addCustomExtension("extra"));
    }

    @Test
    void updateFixedPolicy_updatesEnabledState() {
        fixedRepository.save(new FixedExtensionPolicy("exe", false));

        FixedExtensionPolicy updated = service.updateFixedPolicy("exe", true);

        assertTrue(updated.isEnabled());
        assertTrue(fixedRepository.findById("exe").orElseThrow().isEnabled());
    }

    @Test
    void updateFixedPolicy_rejectsUnknownExtension() {
        assertThrows(NotFoundException.class, () -> service.updateFixedPolicy("exe", true));
    }

    @Test
    void isBlocked_handlesFixedCustomAndInvalid() {
        fixedRepository.save(new FixedExtensionPolicy("exe", true));
        customRepository.save(new CustomBlockedExtension("sh"));

        assertTrue(service.isBlocked("exe"));
        assertTrue(service.isBlocked(".sh"));
        assertTrue(service.isBlocked("bad!"));
        assertFalse(service.isBlocked("txt"));
        assertFalse(service.isBlocked(" "));
    }
}
