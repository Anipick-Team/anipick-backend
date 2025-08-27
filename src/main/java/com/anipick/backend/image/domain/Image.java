package com.anipick.backend.image.domain;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Image {
    private Long imageId;
    private Long authId;
    private String imageName;
    private String imagePath;
}
