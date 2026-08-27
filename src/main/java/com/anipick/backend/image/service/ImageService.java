package com.anipick.backend.image.service;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.image.domain.DefaultImage;
import com.anipick.backend.image.domain.Image;
import com.anipick.backend.image.domain.ImageDefaults;
import com.anipick.backend.image.domain.ImageType;
import com.anipick.backend.image.dto.ImageIdResponse;
import com.anipick.backend.image.mapper.ImageMapper;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final ImageMapper imageMapper;

    private static final Set<String> COMMUNITY_IMAGE_ALLOWED_EXTENSIONS =
            Set.of("png", "jpg", "jpeg", "heic");

    public File compressAndSaveImageToServer(CustomUserDetails user, MultipartFile imageFile) throws IOException {
        String originalFilename = imageFile.getOriginalFilename();
        String uploadImageUrl;

        BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());
        if (bufferedImage == null) {
            throw new CustomException(ErrorCode.INVAILD_IMAGE_EXTENSION);
        }

        byte[] compressedBytes = compressImageWithThumbnailator(imageFile);
        uploadImageUrl = getUploadImageUrl(originalFilename, user.getUserId());

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File outputFile = new File(directory, uploadImageUrl);
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            fileOutputStream.write(compressedBytes);
        }

        return outputFile;
    }

    public Image insertImage(CustomUserDetails user, String originalFilename, File outputFile, ImageType imageType) {
        Image image = Image.builder()
                .authId(user.getUserId())
                .imageName(originalFilename)
                .imagePath(outputFile.getAbsolutePath())
                .imageType(imageType)
                .build();

        imageMapper.insertImage(image);
        return image;
    }

    public Resource getImageResourceOnServer(Long imageId) {
        Optional<Resource> defaultImage = DefaultImage.getImagePath(imageId);
        return defaultImage.orElseGet(() -> {
            final String imagePath = imageMapper.findByImageId(imageId)
                    .map(Image::getImagePath)
                    .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_DATA_NOT_FOUND));
            final Path filePath = Paths.get(imagePath);
            return new FileSystemResource(filePath);
        });
    }

    public String getImageUrlEndpoint(Long imageId) {
        return ImageDefaults.IMAGE_ENDPOINT + imageId;
    }

    public Optional<Image> getImageByImageId(Long imageId) {
        return imageMapper.findByImageId(imageId);
    }

    public Optional<Image> getImageByAuthId(Long userId) {
        return imageMapper.findByUserId(userId);
    }

    @Transactional
    public ImageIdResponse updateProfileImage(CustomUserDetails user, MultipartFile profileImageFile) {
        String originalFilename = profileImageFile.getOriginalFilename();
        Image image;

        try {
            File outputFile = compressAndSaveImageToServer(user, profileImageFile);
            Optional<Image> existedImage = imageMapper.findByUserId(user.getUserId());

            if(existedImage.isPresent()) {
                Image updatedImage = Image.builder()
                        .imageId(existedImage.get().getImageId())
                        .authId(user.getUserId())
                        .imageName(originalFilename)
                        .imagePath(outputFile.getAbsolutePath())
                        .imageType(ImageType.PROFILE)
                        .build();
                updateImage(updatedImage, user.getUserId());

                return ImageIdResponse.from(updatedImage.getImageId());
            } else {
                image = insertImage(user, originalFilename, outputFile, ImageType.PROFILE);

                return ImageIdResponse.from(image.getImageId());
            }
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void updateImage(Image image, Long userId) {
        imageMapper.updateImageByUserId(image, userId);
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
                .size(200, 200)
                .outputQuality(0.7)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * 커뮤니티 게시글 첨부 이미지 업로드. 프로필과 달리 매번 새 image 행을 생성한다(게시글당 최대 5장).
     * 용량 10MB 초과는 spring.servlet.multipart.max-file-size 설정에서 선차단되어
     * MaxUploadSizeExceededException 으로 처리된다.
     */
    @Transactional
    public ImageIdResponse uploadCommunityPostImage(CustomUserDetails user, MultipartFile postImageFile) {
        validateCommunityImageExtension(postImageFile.getOriginalFilename());

        try {
            File outputFile = compressAndSaveCommunityImageToServer(user, postImageFile);
            Image image = insertImage(user, postImageFile.getOriginalFilename(), outputFile, ImageType.COMMUNITY_POST);
            return ImageIdResponse.from(image.getImageId());
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateCommunityImageExtension(String originalFilename) {
        String extension = FilenameUtils.getExtension(
                originalFilename == null ? "" : originalFilename).toLowerCase(Locale.ROOT);
        if (!COMMUNITY_IMAGE_ALLOWED_EXTENSIONS.contains(extension)) {
            throw new CustomException(ErrorCode.INVAILD_IMAGE_EXTENSION);
        }
    }

    private File compressAndSaveCommunityImageToServer(CustomUserDetails user, MultipartFile imageFile) throws IOException {
        String originalFilename = imageFile.getOriginalFilename();

        // TODO(image): heic 는 ImageIO 기본 디코더가 없어 read 결과가 null 이 된다.
        //  현재는 확장자 허용 후 여기서 INVAILD_IMAGE_EXTENSION 으로 떨어짐. heic 지원 시 별도 디코더 필요.
        BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());
        if (bufferedImage == null) {
            throw new CustomException(ErrorCode.INVAILD_IMAGE_EXTENSION);
        }

        byte[] compressedBytes = compressCommunityImageWithThumbnailator(imageFile);
        String uploadImageUrl = getUploadImageUrl(originalFilename, user.getUserId());

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File outputFile = new File(directory, uploadImageUrl);
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            fileOutputStream.write(compressedBytes);
        }

        return outputFile;
    }

    // 게시글 이미지는 프로필(200x200 썸네일)과 달리 본문 표시용이므로 더 큰 해상도를 유지한다.
    private byte[] compressCommunityImageWithThumbnailator(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
                .size(1080, 1080)
                .outputQuality(0.8)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

}
