package com.anipick.backend.log.dto;

import com.anipick.backend.log.domain.UserActionLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LogCreateRequestDto {
    private String action;
    private String page;
    private String area;
    private String type;
    private int animeId;
    private int position;
    private String query;

    public static LogCreateRequestDto from(UserActionLog log) {
        return LogCreateRequestDto.builder()
            .action(log.getActionName())
            .page(log.getPage().name())
            .area(log.getArea().name())
            .type(log.getType())
            .animeId(log.getAnimeId())
            .position(log.getDataBody().getPosition())
            .query(log.getQuery())
            .build();
    }
}
