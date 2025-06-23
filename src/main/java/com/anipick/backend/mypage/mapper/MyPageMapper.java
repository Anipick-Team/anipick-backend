package com.anipick.backend.mypage.mapper;

import com.anipick.backend.mypage.dto.LikedAnimesDto;
import com.anipick.backend.mypage.dto.LikedPersonsDto;
import com.anipick.backend.mypage.dto.WatchListAnimesDto;
import com.anipick.backend.mypage.dto.WatchingAnimesDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MyPageMapper {
    Long getMyWatchCount(@Param("userId") Long userId, @Param("animeStatus") String animeStatus);

    List<LikedAnimesDto> getMyLikedAnimes(@Param("userId") Long userId, @Param("size") Integer size);

    List<LikedPersonsDto> getMyLikedPersons(@Param("userId") Long userId, @Param("size") Integer size);

    List<WatchListAnimesDto> getMyWatchListAnimes(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("lastId") Long lastId,
            @Param("size") Integer size,
            @Param("lastCreatedAt") LocalDateTime lastCreatedAt
    );

    List<WatchingAnimesDto> getMyWatchingAnimes(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("lastId") Long lastId,
            @Param("size") Integer size,
            @Param("lastCreatedAt") LocalDateTime lastCreatedAt
    );
}
