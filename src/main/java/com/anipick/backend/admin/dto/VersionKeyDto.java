package com.anipick.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VersionKeyDto {
    private String platform;
    private long major;
    private long minor;
    private long patch;
    private String type;

    public static VersionKeyDto from(CreateVersionRequestDto dto) {
        return new VersionKeyDto(
                dto.getPlatform(),
                dto.getMajor(),
                dto.getMinor(),
                dto.getPatch(),
                dto.getType()
        );
    }
}
