package com.task.fileuploadapp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fixed_extension_policy")
public class FixedExtensionPolicy {

    @Id
    @Column(length = 20, nullable = false)
    private String extension;

    @Column(nullable = false)
    private boolean enabled;

    protected FixedExtensionPolicy() {
    }

    public FixedExtensionPolicy(String extension, boolean enabled) {
        this.extension = extension;
        this.enabled = enabled;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
