package com.anipick.backend.recommendation.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.anipick.backend.recommendation.dto.AnimeRecommendDto;
import com.anipick.backend.recommendation.dto.RecentHighRequestDto;
import com.anipick.backend.recommendation.dto.TagBasedRequestDto;

@Mapper
public interface RecommendationMapper {
	long countByRecentHigh(RecentHighRequestDto req);

	long countByTagBased(TagBasedRequestDto req);

	List<AnimeRecommendDto> recommendByRecentHigh(RecentHighRequestDto req);

	List<AnimeRecommendDto> recommendByTagBased(TagBasedRequestDto req);
}
