package com.task.fileuploadapp.web.dto;

public class FixedExtensionResponseDto {

    private String extension;
    private boolean enabled;

    public FixedExtensionResponseDto() {
    }

    public FixedExtensionResponseDto(String extension, boolean enabled) {
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
