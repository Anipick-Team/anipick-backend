package com.anipick.backend.admin.dto;

import com.anipick.backend.version.domain.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VersionResultDto {
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

    public static VersionResultDto from(Version version) {
        return new VersionResultDto(
                version.getVersionId(),
                version.getPlatform(),
                version.getMajor(),
                version.getMinor(),
                version.getPatch(),
                importantYnToLong(version.getImportantYn()),
                version.getUrl(),
                version.getType(),
                version.getTitle(),
                version.getUpdatedContent()
        );
    }

    private static Long importantYnToLong(Boolean value) {
        if (value == null) {
            return null;
        }
        if (value) {
            return 1L;
        } else {
            return 0L;
        }
    }
}
