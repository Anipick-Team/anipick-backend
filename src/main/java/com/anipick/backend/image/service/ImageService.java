package com.anipick.backend.image.service;

import app.photofox.vipsffm.VBlob;
import app.photofox.vipsffm.VImage;
import app.photofox.vipsffm.VipsError;
import app.photofox.vipsffm.VipsOption;
import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.image.domain.DefaultImage;
import com.anipick.backend.image.domain.Image;
import com.anipick.backend.image.domain.ImageDefaults;
import com.anipick.backend.image.domain.ImageType;
import com.anipick.backend.image.dto.ImageIdResponse;
import com.anipick.backend.image.mapper.ImageMapper;
import com.anipick.backend.image.storage.ImageStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private static final Set<String> COMMUNITY_IMAGE_ALLOWED_EXTENSIONS =
            Set.of("png", "jpg", "jpeg", "heic");
    private static final int PROFILE_IMAGE_MAX_DIMENSION = 200;
    private static final int PROFILE_IMAGE_QUALITY = 70;
    private static final int COMMUNITY_IMAGE_MAX_DIMENSION = 1080;
    private static final int COMMUNITY_IMAGE_QUALITY = 80;
    private final ImageMapper imageMapper;
    private final ImageStorage imageStorage;

    public Image insertImage(CustomUserDetails user, String originalFilename, String storageKey, ImageType imageType) {
        Image image = Image.builder()
                .authId(user.getUserId())
                .imageName(originalFilename)
                .imagePath(storageKey)
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

            if (imagePath.startsWith("/")) {
                return new FileSystemResource(Paths.get(imagePath));
            }
            return imageStorage.load(imagePath);
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
        final String originalFilename = profileImageFile.getOriginalFilename();
        try {
            final String storageKey = compressAndSave(user, profileImageFile, PROFILE_IMAGE_MAX_DIMENSION, PROFILE_IMAGE_QUALITY);
            Optional<Image> existedImage = imageMapper.findByUserId(user.getUserId());

            if (existedImage.isEmpty()) {
                final Image image = insertImage(user, originalFilename, storageKey, ImageType.PROFILE);
                return ImageIdResponse.from(image.getImageId());
            }
            final Image updatedImage = Image.builder()
                    .imageId(existedImage.get().getImageId())
                    .authId(user.getUserId())
                    .imageName(originalFilename)
                    .imagePath(storageKey)
                    .imageType(ImageType.PROFILE)
                    .build();
            updateImage(updatedImage, user.getUserId());

            return ImageIdResponse.from(updatedImage.getImageId());
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

    private String getUploadImageUrl(final String fileName, final String extension, Long userId) {
        String baseName = FilenameUtils.getBaseName(fileName);
        return userId + System.currentTimeMillis() + baseName + "." + extension;
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
            String storageKey = compressAndSave(user, postImageFile, COMMUNITY_IMAGE_MAX_DIMENSION, COMMUNITY_IMAGE_QUALITY);
            Image image = insertImage(user, postImageFile.getOriginalFilename(), storageKey, ImageType.COMMUNITY_POST);
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

    private String compressAndSave(final CustomUserDetails user, final MultipartFile imageFile, int maxDimension, int quality) throws IOException {
        final CompressedImage compressed = compress(imageFile, maxDimension, quality);
        final String fileName = getUploadImageUrl(imageFile.getOriginalFilename(), compressed.extension(), user.getUserId());
        return imageStorage.save(compressed.bytes(), fileName);
    }

    private CompressedImage compress(final MultipartFile imageFile, final int maxDimension, int quality) throws IOException {
        final byte[] source = imageFile.getBytes();
        try (Arena arena = Arena.ofConfined()) {
            final VImage thumbnail = VImage.thumbnailBuffer(
                    arena, VBlob.newFromBytes(arena, source), maxDimension,
                    VipsOption.Int("height", maxDimension));
            if (thumbnail.hasAlpha()) {
                return new CompressedImage(thumbnail.pngsaveBuffer(VipsOption.Boolean("strip", true)).getBytes(), "png");
            }
            final byte[] jpeg = thumbnail.jpegsaveBuffer(VipsOption.Int("Q", quality), VipsOption.Boolean("strip", true)).getBytes();
            return new CompressedImage(jpeg, "jpg");
        } catch (VipsError e) {
            log.warn("이미지 변환 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVAILD_IMAGE_EXTENSION);
        }
    }

    private record CompressedImage(byte[] bytes, String extension) {
    }
}
