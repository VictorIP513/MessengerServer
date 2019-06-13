package messenger.service;

import messenger.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageService.class);
    private static final String UPLOAD_DIR =
            System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
    private static final String DIALOGS_UPLOAD_DIR = UPLOAD_DIR + "dialogs" + File.separator;


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

    public String storeFileFromDialog(int dialogId, MultipartFile file) {
        String fileExtension = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
        String newFileName = dialogId + UUID.randomUUID().toString() + "." + fileExtension;
        Path newFilePath = Paths.get(DIALOGS_UPLOAD_DIR + newFileName);
        try {
            boolean directoryCreated = new File(DIALOGS_UPLOAD_DIR).mkdirs();
            if (directoryCreated) {
                LOGGER.info("Created dialog upload directory");
            }
            Files.copy(file.getInputStream(), newFilePath);
            return newFileName;
        } catch (IOException e) {
            LOGGER.error("Could not store file {}", file.getOriginalFilename(), e);
        }
        return null;
    }

    public String getFullPathToFile(String pathToFile) {
        return UPLOAD_DIR + pathToFile;
    }

    public String getFullPathToFileInDialog(String pathToFile) {
        return DIALOGS_UPLOAD_DIR + pathToFile;
    }

    public Resource getResourceFromFile(String path) {
        try {
            return new InputStreamResource(new FileInputStream(new File(path)));
        } catch (IOException e) {
            LOGGER.error("File not found: {}", path, e);
        }
        return null;
    }

    void deleteFile(String pathToFile) {
        Path path = Paths.get(UPLOAD_DIR + pathToFile);
        try {
            Files.delete(path);
        } catch (IOException e) {
            LOGGER.warn("File + {} is not deleted", path);
        }
    }
}
