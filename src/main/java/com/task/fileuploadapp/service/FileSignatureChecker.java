package com.task.fileuploadapp.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;

public final class FileSignatureChecker {

    private static final Set<String> EXECUTABLE_MIME_TYPES = Set.of(
            "application/x-msdownload",
            "application/x-dosexec",
            "application/x-msdos-program",
            "application/x-executable",
            "application/x-sh",
            "application/x-shellscript",
            "application/x-bat",
            "application/x-csh",
            "application/x-mach-binary",
            "application/java-archive"
    );

    private FileSignatureChecker() {
    }

    public static boolean isExecutableMime(String contentType) {
        if (contentType == null) {
            return false;
        }
        String normalized = contentType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
        return EXECUTABLE_MIME_TYPES.contains(normalized);
    }

    public static boolean looksExecutable(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return false;
        }
        byte[] header = new byte[8];
        int read;
        try (InputStream stream = file.getInputStream()) {
            read = stream.read(header);
        }
        if (read < 2) {
            return false;
        }
        if (header[0] == 'M' && header[1] == 'Z') {
            return true;
        }
        if (read >= 4 && header[0] == 0x7F && header[1] == 'E' && header[2] == 'L' && header[3] == 'F') {
            return true;
        }
        if (header[0] == '#' && header[1] == '!') {
            return true;
        }
        if (read >= 4) {
            int be = toIntBigEndian(header);
            int le = toIntLittleEndian(header);
            if (isMachO(be) || isMachO(le)) {
                return true;
            }
            if (isMachOFat(be) || isMachOFat(le)) {
                return true;
            }
        }
        return false;
    }

    private static int toIntBigEndian(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24)
                | ((bytes[1] & 0xFF) << 16)
                | ((bytes[2] & 0xFF) << 8)
                | (bytes[3] & 0xFF);
    }

    private static int toIntLittleEndian(byte[] bytes) {
        return ((bytes[3] & 0xFF) << 24)
                | ((bytes[2] & 0xFF) << 16)
                | ((bytes[1] & 0xFF) << 8)
                | (bytes[0] & 0xFF);
    }

    private static boolean isMachO(int magic) {
        return magic == 0xFEEDFACE || magic == 0xCEFAEDFE
                || magic == 0xFEEDFACF || magic == 0xCFFAEDFE;
    }

    private static boolean isMachOFat(int magic) {
        return magic == 0xCAFEBABE || magic == 0xBEBAFECA;
    }
}
