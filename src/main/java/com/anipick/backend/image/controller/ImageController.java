package com.anipick.backend.image.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.image.dto.ImageIdResponse;
import com.anipick.backend.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
@Slf4j
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/{imageId}")
    public ResponseEntity<Resource> getProfileImage(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("imageId") Long imageId
    ) {
        Resource resource = imageService.getImageResourceOnServer(imageId);
        MediaType mediaType = MediaTypeFactory
                .getMediaType(Optional.ofNullable(resource.getFilename()).orElse(""))
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.noCache())
                .body(resource);
    }

    @PostMapping("/profile-image")
    public ApiResponse<ImageIdResponse> updateProfileImage(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestPart("profileImageFile") MultipartFile profileImageFile
    ) {
        ImageIdResponse response = imageService.updateProfileImage(user, profileImageFile);
        return ApiResponse.success(response);
    }
}
