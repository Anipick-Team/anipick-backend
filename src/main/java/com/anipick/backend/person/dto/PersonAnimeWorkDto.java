package com.anipick.backend.person.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PersonAnimeWorkDto {
    private Long animeId;
    private String animeTitle;
    private Long characterId;
    private String characterName;
    private String characterImageUrl;

    public static PersonAnimeWorkDto animeTitleAndCharacterNameTranslationPick(PersonAnimeWorkAllTitleAndNameDto dto) {
        String animeTitle = LocalizationUtil.pickTitle(
                dto.getAnimeTitleKor(),
                dto.getAnimeTitleEng(),
                dto.getAnimeTitleRom(),
                dto.getAnimeTitleNat()
        );

        String characterName = LocalizationUtil.pickCharacterName(
                dto.getCharacterNameKor(),
                dto.getCharacterNameEng()
        );

        return new PersonAnimeWorkDto(
                dto.getAnimeId(),
                animeTitle,
                dto.getCharacterId(),
                characterName,
                dto.getCharacterImageUrl()
        );
    }
}
