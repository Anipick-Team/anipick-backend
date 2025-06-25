package com.anipick.backend.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyPageResponse {
    private String nickname;
    private String profileImageUrl;
    private WatchCountDto watchCounts;
    private List<LikedAnimesDto> likedAnimes;
    private List<LikedPersonsDto> likedPersons;

    public static MyPageResponse from(String nickname, String profileImageUrl, WatchCountDto watchCounts, List<LikedAnimesDto> likedAnimes, List<LikedPersonsDto> likedPersons) {
        return new MyPageResponse(nickname, profileImageUrl, watchCounts, likedAnimes, likedPersons);
    }
}
