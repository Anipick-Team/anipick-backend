package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LikedPersonsResponse {
    private Long count;
    private CursorDto cursor;
    private List<LikedPersonsDto> persons;

    public static LikedPersonsResponse from(Long count, CursorDto cursorDto, List<LikedPersonsDto> persons) {
        return new LikedPersonsResponse(count, cursorDto, persons);
    }
}
