package com.example.KFCC_Backend.Utility;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FileStorageUtil {

    private static final String BASE_PATH = "uploads/membership/documents";

    public String saveFile(String applicationId, String subFolder, MultipartFile file)
            throws IOException {

        if (file == null || file.isEmpty()) return null;

        String dirPath = BASE_PATH + "/" + applicationId + "/" + subFolder;
        Path directory = Paths.get(dirPath);

        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = directory.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

}
