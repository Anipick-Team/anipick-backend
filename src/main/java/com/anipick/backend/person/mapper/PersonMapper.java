package com.anipick.backend.person.mapper;

import com.anipick.backend.person.dto.PersonAnimeWorkDto;
import com.anipick.backend.person.dto.PersonInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PersonMapper {
    PersonInfoDto selectActorInfo(
            @Param(value = "personId") Long personId,
            @Param(value = "userId") Long userId
    );

    long countAnimesByActor(@Param(value = "personId") Long personId);

    List<PersonAnimeWorkDto> selectWorksByActor(
            @Param(value = "personId") Long personId,
            @Param(value = "lastId") Long lastId,
            @Param(value = "size") int size
    );
}
