package com.anipick.backend.person.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class PersonDetailPageDto {
    private Long personId;
    private String name;
    private String profileImageUrl;
    private Boolean isLiked;
    private long count;
    private CursorDto cursor;
    private List<PersonAnimeWorkDto> works;
}
