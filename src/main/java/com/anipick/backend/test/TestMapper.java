package com.anipick.backend.test;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.recommendation.dto.AnimeRecommendDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestMapper {
    List<TestResponseDto> findReviews(@Param(value = "userId") long userId);

    List<AnimeItemDto> selectReferenceAnime(@Param(value = "referenceAnimeId") Long referenceAnimeId);

    List<TestAnimeTagDto> getAnimeTags(@Param(value = "animeId") Long animeId);
}
