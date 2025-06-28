package com.anipick.backend.person.service;

import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.person.dto.PersonAnimeWorkDto;
import com.anipick.backend.person.dto.PersonDetailPageDto;
import com.anipick.backend.person.dto.PersonInfoDto;
import com.anipick.backend.person.mapper.PersonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonMapper personMapper;

    public PersonDetailPageDto getAnimeAndCharacter(Long personId, Long lastId, int size, Long userId) {
        PersonInfoDto personInfoDto = personMapper.selectActorInfo(personId, userId);

        long totalCount = personMapper.countAnimesByActor(personId);

        List<PersonAnimeWorkDto> items = personMapper.selectWorksByActor(personId, lastId, size);

        Long nextId;
        if (items.isEmpty()) {
            nextId = null;
        } else {
            nextId = items.getLast().getAnimeId();
        }
        CursorDto cursor = CursorDto.of(nextId);

        return PersonDetailPageDto.of(
                personInfoDto.getPersonId(),
                personInfoDto.getName(),
                personInfoDto.getProfileImageUrl(),
                personInfoDto.getIsLiked(),
                totalCount,
                cursor,
                items
        );
    }
}
