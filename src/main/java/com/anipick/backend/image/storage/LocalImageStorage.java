package com.anipick.backend.image.storage;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

@Component
@ConditionalOnProperty(name = "oci.object-storage.enabled", havingValue = "false", matchIfMissing = true)
public class LocalImageStorage implements ImageStorage {

    private final String uploadDir;

    public LocalImageStorage(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override
    public String save(byte[] data, String fileName) {
        File directory = new File(uploadDir);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new CustomException(ErrorCode.IMAGE_DATA_NOT_FOUND);
        }
        File outputFile = new File(directory, fileName);
        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            out.write(data);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IMAGE_DATA_NOT_FOUND);
        }
        return fileName;
    }

    @Override
    public Resource load(String key) {
        return new FileSystemResource(Paths.get(uploadDir, key));
    }
}
