package com.anipick.backend.version.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoticeUpdateResultDto {
    private String type;
    private String title;
    private String content;
    private String url;

    public static NoticeUpdateResultDto of(NoticeUpdateItemDto dto) {
        return new NoticeUpdateResultDto(dto.getType(), dto.getTitle(), dto.getContent(), dto.getUrl());
    }
}
