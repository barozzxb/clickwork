package vn.clickwork.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Store a file in the specified directory
     *
     * @param file The file to store
     * @param directory The directory to store the file in
     * @return The URL of the stored file
     * @throws Exception If an error occurs during file storage
     */
    String storeFile(MultipartFile file, String directory) throws Exception;

    /**
     * Delete a file
     *
     * @param fileUrl The URL of the file to delete
     * @return True if the file was deleted successfully
     */
    boolean deleteFile(String fileUrl);

    /**
     * Get the file extension
     *
     * @param fileName The name of the file
     * @return The file extension
     */
    String getFileExtension(String fileName);
}
