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

    Boolean selectUserLikeActor(
            @Param(value = "userId") Long userId,
            @Param(value = "personId") Long personId
    );

    void insertLikeActor(
            @Param(value = "userId") Long userId,
            @Param(value = "personId") Long personId
    );

    void deleteLikeActor(
            @Param(value = "userId") Long userId,
            @Param(value = "personId") Long personId
    );

    Boolean selectUserLikeReview(
            @Param(value = "userId") Long userId,
            @Param(value = "reviewId") Long reviewId
    );

    void insertLikeReview(
            @Param(value = "userId") Long userId,
            @Param(value = "reviewId") Long reviewId
    );

    void deleteLikeReview(
            @Param(value = "userId") Long userId,
            @Param(value = "reviewId") Long reviewId
    );

    void deleteAllLikeReview(@Param(value = "reviewId") Long reviewId);
}
