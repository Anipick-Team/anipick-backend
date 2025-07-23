package com.anipick.backend.like.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeMapper {
    Boolean selectUserLikeAnime(
            @Param(value = "userId") Long userId,
            @Param(value = "animeId") Long animeId
    );

    void insertLikeAnime(
            @Param(value = "userId") Long userId,
            @Param(value = "animeId") Long animeId
    );

    void deleteLikeAnime(
            @Param(value = "userId") Long userId,
            @Param(value = "animeId") Long animeId
    );
}
