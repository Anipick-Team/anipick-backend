package com.anipick.backend.version.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VersionResultDto {
    private Boolean isRequiredUpdate;
    private String type;
    private String title;
    private String content;
    private String url;

    public static VersionResultDto from(Boolean isRequiredUpdate, NoticeUpdateItemDto item) {
        if (item == null) {
            return new VersionResultDto(isRequiredUpdate, null, null,null, null);
        }
        return new VersionResultDto(isRequiredUpdate, item.getType(), item.getTitle(), item.getContent(), item.getUrl());
    }
}
