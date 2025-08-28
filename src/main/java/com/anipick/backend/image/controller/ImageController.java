package com.anipick.backend.image.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
@Slf4j
public class ImageController {
    private final ImageService imageService;

    @GetMapping("{imageId}")
    public ResponseEntity<Resource> getProfileImage(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("imageId") Long imageId
    ) {
        try {
            Resource resource = imageService.getImageResourceOnServer(imageId);
            String contentType = Files.probeContentType(resource.getFile().toPath());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            log.error("IO error : ", e);
        }

        return ResponseEntity.notFound().build();
    }
}
