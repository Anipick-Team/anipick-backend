package com.anipick.backend.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WatchCountDto {
    private Long watchList;
    private Long watching;
    private Long finished;

    public static WatchCountDto from(Long watchList, Long watching, Long finished) {
        return new WatchCountDto(watchList, watching, finished);
    }
}
