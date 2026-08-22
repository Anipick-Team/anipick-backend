package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyCommunityPostsResponse {
    private Long count;
    private CursorDto cursor;
    private List<MyCommunityPostDto> posts;

    public static MyCommunityPostsResponse from(Long count, CursorDto cursor, List<MyCommunityPostDto> posts) {
        return new MyCommunityPostsResponse(count, cursor, posts);
    }
}
