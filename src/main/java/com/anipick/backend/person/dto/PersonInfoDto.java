package com.anipick.backend.person.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PersonInfoDto {
    private Long personId;
    private String name;
    private String profileImageUrl;
    private Boolean isLiked;

    public static PersonInfoDto personNameTranslationPick(PersonInfoAllNameDto dto) {
        String name = LocalizationUtil.pickVoiceActorName(
                dto.getNameKor(),
                dto.getNameEng()
        );
        return new PersonInfoDto(dto.getPersonId(), name, dto.getProfileImageUrl(), dto.getIsLiked());
    }
}
