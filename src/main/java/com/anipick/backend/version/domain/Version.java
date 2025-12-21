package com.anipick.backend.version.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Version {
    private Long versionId;
    private String platform;
    private Long major;
    private Long minor;
    private Long patch;
    private Boolean importantYn;
    private String url;
    private String type;
    private String title;
    private String updatedContent;
    private LocalDateTime updatedAt;
}
