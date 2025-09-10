package com.anipick.backend.person.service;

import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.util.LocalizationUtil;
import com.anipick.backend.person.dto.*;
import com.anipick.backend.person.mapper.PersonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonMapper personMapper;

    public PersonDetailPageDto getAnimeAndCharacter(Long personId, Long lastId, int size, Long userId) {
        PersonInfoAllNameDto personInfoDto = personMapper.selectActorInfo(personId, userId);

        String name = LocalizationUtil.pickVoiceActorName(
                personInfoDto.getNameKor(),
                personInfoDto.getNameEng()
        );

        long totalCount = personMapper.countAnimesByActor(personId);

        List<PersonAnimeWorkDto> items = personMapper.selectWorksByActor(personId, lastId, size)
                .stream()
                .map(PersonAnimeWorkDto::animeTitleAndCharacterNameTranslationPick)
                .toList();

        Long nextId;
        if (items.isEmpty()) {
            nextId = null;
        } else {
            nextId = items.getLast().getAnimeId();
        }
        CursorDto cursor = CursorDto.of(nextId);

        return PersonDetailPageDto.of(
                personInfoDto.getPersonId(),
                name,
                personInfoDto.getProfileImageUrl(),
                personInfoDto.getIsLiked(),
                totalCount,
                cursor,
                items
        );
    }
}
