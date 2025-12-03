package com.anipick.backend.version.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class NoticeUpdateItemDto {
    private Integer majorVersion;
    private Integer minorVersion;
    private Integer patchVersion;
    private String type;
    private String title;
    private String content;
    private String url;
}
