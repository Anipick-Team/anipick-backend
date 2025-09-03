package com.anipick.backend.image.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageIdResponse {
    private Long imageId;

    public static ImageIdResponse from(Long imageId) {
        return new ImageIdResponse(imageId);
    }
}
