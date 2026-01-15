package com.task.fileuploadapp.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.task.fileuploadapp.domain.CustomBlockedExtension;
import com.task.fileuploadapp.domain.FixedExtensionPolicy;
import com.task.fileuploadapp.service.ExtensionPolicyService;
import com.task.fileuploadapp.service.FileSignatureChecker;
import com.task.fileuploadapp.service.exception.BadRequestException;
import com.task.fileuploadapp.web.dto.AddCustomRequestDto;
import com.task.fileuploadapp.web.dto.CustomExtensionResponseDto;
import com.task.fileuploadapp.web.dto.FixedExtensionResponseDto;
import com.task.fileuploadapp.web.dto.UpdateFixedRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ExtensionPolicyApiController {

    private final ExtensionPolicyService service;

    public ExtensionPolicyApiController(ExtensionPolicyService service) {
        this.service = service;
    }

    @GetMapping("/extensions/fixed")
    public List<FixedExtensionResponseDto> getFixedExtensions() {
        return service.getFixedPolicies().stream()
                .map(policy -> new FixedExtensionResponseDto(policy.getExtension(), policy.isEnabled()))
                .toList();
    }

    @PutMapping("/extensions/fixed/{ext}")
    public FixedExtensionResponseDto updateFixedExtension(
        @PathVariable("ext") String ext,
        @RequestBody UpdateFixedRequest request
    ) {
        FixedExtensionPolicy updated = service.updateFixedPolicy(ext, request.isEnabled());
        return new FixedExtensionResponseDto(updated.getExtension(), updated.isEnabled());
    }

    @GetMapping("/extensions/custom")
    public List<CustomExtensionResponseDto> getCustomExtensions() {
        return service.getCustomExtensions().stream()
                .map(this::toCustomDto)
                .toList();
    }

    @PostMapping("/extensions/custom")
    public CustomExtensionResponseDto addCustomExtension(@RequestBody AddCustomRequestDto request) {
        if (request == null || request.getExtension() == null) {
            throw new BadRequestException("Extension is required.");
        }
        CustomBlockedExtension added = service.addCustomExtension(request.getExtension());
        return toCustomDto(added);
    }

    @DeleteMapping("/extensions/custom/{id}")
    public void deleteCustomExtension(@PathVariable("id") Long id) {
        service.deleteCustomExtension(id);
    }

    private CustomExtensionResponseDto toCustomDto(CustomBlockedExtension entity) {
        String createdAt = entity.getCreatedAt() == null
                ? null
                : entity.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new CustomExtensionResponseDto(entity.getId(), entity.getExtension(), createdAt);
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(
        @RequestParam("file") MultipartFile file
    ) throws IOException {
        String filename = file.getOriginalFilename();

        if (filename == null || !filename.contains(".")) {
            throw new BadRequestException("File has no extension.");
        }
        String ext = filename.substring(filename.lastIndexOf('.'));
        if (service.isBlocked(ext)) {
            throw new BadRequestException("Blocked extension: " + ext);
        }
        if (FileSignatureChecker.isExecutableMime(file.getContentType())
                || FileSignatureChecker.looksExecutable(file)) {
            throw new BadRequestException("Potential executable content detected.");
        }
        Path temp = Files.createTempFile("upload-", "-" + filename.replaceAll("[^a-zA-Z0-9._-]", "_"));
        file.transferTo(temp);
        return ResponseEntity.ok("Uploaded to: " + temp.toAbsolutePath());
    }
}
