package com.anipick.backend.version.dto;

import com.anipick.backend.oauth.domain.Platform;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAppVersionRequestDto {
    private Integer majorVersion;
    private Integer minorVersion;
    private Integer patchVersion;
    private String platform;

    public static UserAppVersionRequestDto from(String version, Platform platform) {
        String[] versionParts = version.split("\\.");

        Integer major = Integer.parseInt(versionParts[0]);
        Integer minor = Integer.parseInt(versionParts[1]);
        Integer patch = Integer.parseInt(versionParts[2]);
        String platformName = platform.name();

        return new UserAppVersionRequestDto(major, minor, patch, platformName);
    }
}
