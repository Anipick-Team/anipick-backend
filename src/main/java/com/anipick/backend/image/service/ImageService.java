package com.anipick.backend.image.service;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.image.domain.Image;
import com.anipick.backend.image.mapper.ImageMapper;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ImageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final ImageMapper imageMapper;

    public File compressAndSaveImageToServer(CustomUserDetails user, MultipartFile imageFile) throws IOException {
        String originalFilename = imageFile.getOriginalFilename();
        String uploadImageUrl;

        BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());
        if(bufferedImage == null) {
            throw new CustomException(ErrorCode.INVAILD_IMAGE_EXTENSION);
        }

        byte[] compressedBytes = compressImageWithThumbnailator(imageFile);
        uploadImageUrl = getUploadImageUrl(originalFilename, user.getUserId());

        File directory = new File(uploadDir);
        if(!directory.exists()) {
            directory.mkdirs();
        }

        File outputFile = new File(directory, uploadImageUrl);
        try(FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            fileOutputStream.write(compressedBytes);
        }

        return outputFile;
    }

    public Image insertImage(CustomUserDetails user, String originalFilename, File outputFile) {
        Image image = Image.builder()
                .authId(user.getUserId())
                .imageName(originalFilename)
                .imagePath(outputFile.getAbsolutePath())
                .build();

        imageMapper.insertImage(image);
        return image;
    }

    public Resource getImageResourceOnServer(Long imageId) {
        Image image = imageMapper.findByImageId(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_DATA_NOT_FOUND));
        String imagePath = image.getImagePath();
        Path filePath = Paths.get(imagePath);

        return new FileSystemResource(filePath);
    }

    public Image getImageByImageId(Long imageId) {
        return imageMapper.findByImageId(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_DATA_NOT_FOUND));
    }

    public Image getImageByAuthId(Long userId) {
        return imageMapper.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_DATA_NOT_FOUND));
    }

    public void deleteImage(Long imageId) {
        imageMapper.deleteImage(imageId);
    }

    private String getUploadImageUrl(String fileName, Long userId) {
        String baseName = FilenameUtils.getBaseName(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        return userId + System.currentTimeMillis() + baseName + "." + extension;
    }

    private byte[] compressImageWithThumbnailator(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
                .size(800, 800)
                .outputQuality(0.7)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }
}
