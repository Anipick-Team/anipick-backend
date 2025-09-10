package com.anipick.backend.recommendation.mapper;

import com.anipick.backend.recommendation.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecommendMapper {

    List<AnimeAllTitleItemRecommendTagCountDto> selectUserRecentHighAnimes(RecentHighCountOnlyRequest request);

    List<TagScoreDto> selectTagScoresForUser(
            @Param(value = "userId") Long userId,
            @Param(value = "animeIds") List<Long> filteredIds
    );

    List<AnimeAllTitleItemRecommendTagCountDto> selectTagBasedAnimes(TagBasedCountOnlyRequest request);
}
