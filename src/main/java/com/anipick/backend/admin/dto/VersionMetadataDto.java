package com.anipick.backend.admin.dto;

import com.anipick.backend.version.domain.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class VersionMetadataDto {
    private Long versionId;
    private Long major;
    private Long minor;
    private Long patch;
    private String type;
    private LocalDate updatedAt;

    public static VersionMetadataDto of(Version version) {
        return new VersionMetadataDto(
                version.getVersionId(),
                version.getMajor(),
                version.getMinor(),
                version.getPatch(),
                version.getType(),
                version.getUpdatedAt().toLocalDate()
        );
    }
}
