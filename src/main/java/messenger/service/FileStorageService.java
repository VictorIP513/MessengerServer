package messenger.service;

import messenger.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageService.class);
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    public String storeFile(MultipartFile file, User user) {
        String userUUID = user.getUuid().toString();
        String fileExtension = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
        Path newFilePath = Paths.get(UPLOAD_DIR + userUUID + File.separator + newFileName);
        try {
            boolean directoryCreated = new File(UPLOAD_DIR + File.separator + userUUID).mkdirs();
            if (directoryCreated) {
                LOGGER.info("Created user upload directory: {}", userUUID);
            }
            Files.copy(file.getInputStream(), newFilePath);
            return userUUID + File.separator + newFileName;
        } catch (IOException e) {
            LOGGER.error("Could not store file {}", file.getOriginalFilename(), e);
        }
        return null;
    }

    public void deleteFile(String pathToFile) {
        Path path = Paths.get(UPLOAD_DIR + pathToFile);
        try {
            Files.delete(path);
        } catch (IOException e) {
            LOGGER.warn("File + {} is not deleted", path);
        }
    }
}
