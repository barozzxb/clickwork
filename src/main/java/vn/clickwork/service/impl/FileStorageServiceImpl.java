package vn.clickwork.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import vn.clickwork.service.FileStorageService;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.base-url}")
    private String baseUrl;

    @Override
    public String storeFile(MultipartFile file, String directory) throws Exception {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Check if the file's name contains invalid characters
        if (originalFileName.contains("..")) {
            throw new Exception("Filename contains invalid path sequence: " + originalFileName);
        }

        // Generate a unique file name to prevent duplicates
        String fileExtension = getFileExtension(originalFileName);
        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;

        // Create the full path - ensure we use /Uploads/avatar/ for avatar files
        String storagePath = directory.equals("avatars")
                ? uploadDir + "/avatar"
                : uploadDir + "/" + directory;

        // Create the directory if it doesn't exist
        Path directoryPath = Paths.get(storagePath).toAbsolutePath().normalize();
        Files.createDirectories(directoryPath);

        // Copy the file to the target location
        Path targetLocation = directoryPath.resolve(newFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Return the URL of the file
        String fileUrl = directory.equals("avatars")
                ? baseUrl + "/avatar/" + newFileName
                : baseUrl + "/" + directory + "/" + newFileName;

        return fileUrl;
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            // Extract the file path from the URL
            String filePath;
            if (fileUrl.contains("/avatar/")) {
                // Handle avatar files specifically
                String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                filePath = "/Uploads/avatar/" + fileName;
            } else {
                // Handle other files
                filePath = fileUrl.replace(baseUrl, uploadDir);
            }

            Path path = Paths.get(filePath);

            // Delete the file
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
}
