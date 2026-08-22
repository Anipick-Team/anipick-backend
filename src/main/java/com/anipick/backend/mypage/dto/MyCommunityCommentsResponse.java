package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyCommunityCommentsResponse {
    private Long count;
    private CursorDto cursor;
    private List<MyCommunityCommentDto> comments;

    public static MyCommunityCommentsResponse from(Long count, CursorDto cursor, List<MyCommunityCommentDto> comments) {
        return new MyCommunityCommentsResponse(count, cursor, comments);
    }
}
