package com.anipick.backend.user.mapper;

import com.anipick.backend.review.dto.UserAnimeStatusInsertRequest;
import com.anipick.backend.user.domain.UserAnimeOfStatus;
import com.anipick.backend.user.domain.UserAnimeStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    void deleteUserAnimeStatus(
            @Param(value = "userId") Long userId,
            @Param(value = "animeId") Long animeId
    );

    void createUserAnimeStatusBulk(@Param(value = "items") List<UserAnimeStatusInsertRequest> statusBulkList);
}
