package com.task.fileuploadapp.web;

import org.springframework.web.bind.annotation.*;

import com.task.fileuploadapp.domain.FixedExtensionPolicy;
import com.task.fileuploadapp.service.ExtensionPolicyService;
import com.task.fileuploadapp.web.dto.FixedExtensionResponseDto;
import com.task.fileuploadapp.web.dto.UpdateFixedRequest;


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
}
