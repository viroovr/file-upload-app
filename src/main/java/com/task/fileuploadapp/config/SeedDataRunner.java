package com.task.fileuploadapp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.task.fileuploadapp.domain.FixedExtensionPolicy;
import com.task.fileuploadapp.repository.FixedExtensionPolicyRepository;

import java.util.List;

@Component
public class SeedDataRunner implements CommandLineRunner {

    private static final List<String> DEFAULT_FIXED = List.of(
            "exe", "bat", "cmd", "com", "cpl", "scr", "js"
    );

    private final FixedExtensionPolicyRepository repository;

    public SeedDataRunner(FixedExtensionPolicyRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        for (String extension : DEFAULT_FIXED) {
            if (!repository.existsById(extension)) {
                repository.save(new FixedExtensionPolicy(extension, false));
            }
        }
    }
}
