package com.task.fileuploadapp.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

class FileSignatureCheckerTest {

    @Test
    void isExecutableMime_detectsKnownTypes() {
        assertTrue(FileSignatureChecker.isExecutableMime("application/x-msdownload"));
        assertTrue(FileSignatureChecker.isExecutableMime("application/x-sh; charset=UTF-8"));
        assertFalse(FileSignatureChecker.isExecutableMime("image/png"));
        assertFalse(FileSignatureChecker.isExecutableMime(null));
    }

    @Test
    void looksExecutable_detectsPeHeader() throws IOException {
        byte[] content = new byte[] { 'M', 'Z', 0x00, 0x00 };
        MockMultipartFile file = new MockMultipartFile("file", "demo.bin", "application/octet-stream", content);

        assertTrue(FileSignatureChecker.looksExecutable(file));
    }

    @Test
    void looksExecutable_detectsShebang() throws IOException {
        byte[] content = "#!/bin/sh\n".getBytes(StandardCharsets.US_ASCII);
        MockMultipartFile file = new MockMultipartFile("file", "script.txt", "text/plain", content);

        assertTrue(FileSignatureChecker.looksExecutable(file));
    }

    @Test
    void looksExecutable_returnsFalseForSafeText() throws IOException {
        byte[] content = "hello".getBytes(StandardCharsets.US_ASCII);
        MockMultipartFile file = new MockMultipartFile("file", "note.txt", "text/plain", content);

        assertFalse(FileSignatureChecker.looksExecutable(file));
    }
}
