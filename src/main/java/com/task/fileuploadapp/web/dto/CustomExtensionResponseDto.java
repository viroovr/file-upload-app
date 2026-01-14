package com.task.fileuploadapp.web.dto;

public class CustomExtensionResponseDto {

    private Long id;
    private String extension;
    private String createdAt;

    public CustomExtensionResponseDto() {
    }

    public CustomExtensionResponseDto(Long id, String extension, String createdAt) {
        this.id = id;
        this.extension = extension;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
