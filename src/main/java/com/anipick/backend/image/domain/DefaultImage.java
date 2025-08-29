package com.anipick.backend.image.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum DefaultImage {
    USER_DEFAULT_IMAGE(-1,"static/image/default.jpg"),;
    private final long imageId;
    private final String imagePath;
    private static final Map<Long, String> IMAGE_ID_BY_IMAGE_PATH = Arrays.stream(DefaultImage.values())
            .collect(Collectors.toMap(DefaultImage::getImageId, DefaultImage::getImagePath));

    public static Optional<Resource> getImagePath(Long imageId) {
        final String path = IMAGE_ID_BY_IMAGE_PATH.get(imageId);
        if (path == null) {
            return Optional.empty();
        }
        final Path filePath = Paths.get(path);
        return Optional.of(new ClassPathResource(filePath.toString()));
    }
}
