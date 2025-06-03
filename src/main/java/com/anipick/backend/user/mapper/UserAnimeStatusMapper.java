package com.anipick.backend.user.mapper;

import com.anipick.backend.user.domain.UserAnimeOfStatus;
import com.anipick.backend.user.domain.UserAnimeStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserAnimeStatusMapper {
    UserAnimeStatus findByUserId(
            @Param(value = "userId") Long userId,
            @Param(value = "animeId") Long animeId
    );

    void updateUserAnimeStatus(
            @Param(value = "userId") Long userId,
            @Param(value = "animeId") Long animeId,
            @Param(value = "status") UserAnimeOfStatus status
    );

    void createUserAnimeStatus(
            @Param(value = "userId") Long userId,
            @Param(value = "animeId") Long animeId,
            @Param(value = "status") UserAnimeOfStatus status
    );
}
