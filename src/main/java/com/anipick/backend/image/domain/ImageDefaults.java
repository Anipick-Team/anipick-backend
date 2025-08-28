package com.anipick.backend.image.domain;

import lombok.Getter;

@Getter
public class ImageDefaults {
    public static final String HTTP_IMAGE_ENDPOINT = "http://anipick.p-e.kr:8080/api/image/";
    public static final String HTTPS_IMAGE_ENDPOINT = "https://anipick.p-e.kr/api/image/";
    public static final String DEFAULT_PROFILE_IMAGE_PATH = "/image/default.jpg";
}
