package com.anipick.backend.recommendation.mapper;

import com.anipick.backend.recommendation.dto.AnimeItemRecommendTagCountDto;
import com.anipick.backend.recommendation.dto.RecentHighCountOnlyRequest;
import com.anipick.backend.recommendation.dto.TagBasedCountOnlyRequest;
import com.anipick.backend.recommendation.dto.TagScoreDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecommendMapper {

    List<AnimeItemRecommendTagCountDto> selectUserRecentHighAnimes(RecentHighCountOnlyRequest request);

    List<TagScoreDto> selectTagScoresForUser(
            @Param(value = "userId") Long userId,
            @Param(value = "animeIds") List<Long> filteredIds
    );

    List<AnimeItemRecommendTagCountDto> selectTagBasedAnimes(TagBasedCountOnlyRequest request);
}
