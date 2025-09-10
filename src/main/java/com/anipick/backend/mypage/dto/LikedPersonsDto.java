package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikedPersonsDto {
    private Long personId;
    private Long userLikedVoiceActorId;
    private String name;
    private String profileImageUrl;

    public static LikedPersonsDto personNameTranslationPick(LikedPersonsAllNameDto dto) {
        String name = LocalizationUtil.pickVoiceActorName(
                dto.getNameKor(),
                dto.getNameEng()
        );
        return new LikedPersonsDto(dto.getPersonId(), dto.getUserLikedVoiceActorId(), name, dto.getProfileImageUrl());
    }
}
