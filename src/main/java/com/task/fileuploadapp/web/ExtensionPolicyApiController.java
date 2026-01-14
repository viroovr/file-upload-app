package com.task.fileuploadapp.web;

import org.springframework.web.bind.annotation.*;

import com.task.fileuploadapp.domain.CustomBlockedExtension;
import com.task.fileuploadapp.domain.FixedExtensionPolicy;
import com.task.fileuploadapp.service.ExtensionPolicyService;
import com.task.fileuploadapp.service.exception.BadRequestException;
import com.task.fileuploadapp.web.dto.AddCustomRequest;
import com.task.fileuploadapp.web.dto.CustomExtensionDto;
import com.task.fileuploadapp.web.dto.FixedExtensionResponseDto;
import com.task.fileuploadapp.web.dto.UpdateFixedRequest;

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
    public List<CustomExtensionDto> getCustomExtensions() {
        return service.getCustomExtensions().stream()
                .map(this::toCustomDto)
                .toList();
    }

    @PostMapping("/extensions/custom")
    public CustomExtensionDto addCustomExtension(@RequestBody AddCustomRequest request) {
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

    private CustomExtensionDto toCustomDto(CustomBlockedExtension entity) {
        String createdAt = entity.getCreatedAt() == null
                ? null
                : entity.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new CustomExtensionDto(entity.getId(), entity.getExtension(), createdAt);
    }
}