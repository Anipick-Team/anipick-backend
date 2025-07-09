package com.anipick.backend.recommendation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecommendReviewUserMapper {
    Long findMostRecentHighRateAnime(@Param(value = "userId") Long userId);

    List<Long> findTopRatedAnimeIds(
            @Param("userId") Long userId,
            @Param("limit")  int limit
    );
}
