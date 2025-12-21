package com.anipick.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateVersionRequestDto {
    private Long versionId;
    private String platform;
    private Long major;
    private Long minor;
    private Long patch;
    private Long importantYn;
    private String url;
    private String type;
    private String title;
    private String updatedContent;

    public static UpdateVersionRequestDto of(Long versionId, CreateVersionRequestDto dto) {
        return new UpdateVersionRequestDto(
                versionId,
                dto.getPlatform(),
                dto.getMajor(),
                dto.getMinor(),
                dto.getPatch(),
                dto.getImportantYn(),
                dto.getUrl(),
                dto.getType(),
                dto.getTitle(),
                dto.getUpdatedContent()
        );
    }
}
