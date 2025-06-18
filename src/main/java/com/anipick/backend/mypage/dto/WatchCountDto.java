package com.anipick.backend.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WatchCountDto {
    private Long watchList;
    private Long watching;
    private Long watched;

    public static WatchCountDto of(Long watchList, Long watching, Long watched) {
        return new WatchCountDto(watchList, watching, watched);
    }
}
