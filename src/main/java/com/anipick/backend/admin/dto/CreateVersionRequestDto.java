package com.anipick.backend.admin.dto;

import lombok.Getter;

@Getter
public class CreateVersionRequestDto {
    private String platform;
    private long major;
    private long minor;
    private long patch;
    private long importantYn;
    private String url;
    private String type;
    private String title;
    private String updatedContent;
}
