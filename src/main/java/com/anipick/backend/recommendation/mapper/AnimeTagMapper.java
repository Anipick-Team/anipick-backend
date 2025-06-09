package com.anipick.backend.recommendation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnimeTagMapper {
    List<Long> findTopTagsByAnime(
            @Param(value = "animeId") Long referenceAnimeId,
            @Param(value = "limit") int limit
    );
}
