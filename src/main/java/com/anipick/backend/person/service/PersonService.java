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
        // TODO : 성우의 정보 가져오기 (ID, NAME, PROFILE_URL, USER_IS_LIKED) - 파라미터 personId, userId
        PersonInfoDto personInfoDto = personMapper.selectActorInfo(personId, userId);
        // TODO : 성우가 참여한 애니들 개수 가져오기 - 파라미터 personId
        long totalCount = personMapper.countAnimesByActor(personId);
        // TODO : 성우가 참여한 애니 리스트 가져오기 - 파라미터 personId, lastId, size
        List<PersonAnimeWorkDto> items = personMapper.selectWorksByActor(personId, lastId, size);
        // TODO : Cursor 객체 조립 후 PersonDetailPageDto 객체 조립 -> 리턴
        Long nextId;
        if (items.isEmpty()) {
            nextId = null;
        } else {
            nextId = items.getLast().getAnimeId();
        }
        CursorDto cursor = CursorDto.of(lastId);
        // TODO : 조립
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
