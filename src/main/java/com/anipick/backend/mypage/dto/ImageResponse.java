package com.anipick.backend.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageResponse {
    private byte[] imageBytes;

    public static ImageResponse from(byte[] imageBytes) {
        return new ImageResponse(imageBytes);
    }
}
