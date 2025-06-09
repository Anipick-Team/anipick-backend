package com.anipick.backend.recommendation.mapper;

import java.util.List;

import com.anipick.backend.recommendation.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecommendationMapper {
	long countByRecentHigh(RecentHighRequestDto req);

	long countByTagBased(TagBasedRequestDto req);

	List<AnimeRecommendDto> recommendByRecentHigh(RecentHighRequestDto req);

	List<AnimeRecommendDto> recommendByTagBased(TagBasedRequestDto req);







	/**
	 * Full 일 때 tag_count + total_score 모두 반환
	 */
	List<AnimeRecommendDto2> selectRecentHighFull(RecentHighFullRequest req);
	List<AnimeRecommendDto2> selectRecentHighFull2(RecentHighFullRequest req);
	long countRecentHighFull(RecentHighFullRequest req);

	/**
	 * Count 일 때 tag_count 만 반환
	 */
	List<AnimeRecommendDto2> selectRecentHighCountOnly(RecentHighCountOnlyRequest req);
	long countRecentHighCountOnly(RecentHighCountOnlyRequest req);

	/**
	 * Score 일 때 total_score 만 반환
	 */
	List<AnimeRecommendDto2> selectRecentHighScoreOnly(RecentHighScoreOnlyRequest req);
	long countRecentHighScoreOnly(RecentHighScoreOnlyRequest req);

	/**
	 * Tag-Based Full 일 때 tag_count + total_score 반환
	 */
	List<AnimeRecommendDto2> selectTagBasedFull(TagBasedFullRequest req);
	List<AnimeRecommendDto2> selectTagBasedFull2(TagBasedFullRequest req);
	long countTagBasedFull(TagBasedFullRequest req);

	/**
	 * Tag-Based Count 일 때 tag_count 만 반환
	 */
	List<AnimeRecommendDto2> selectTagBasedCountOnly(TagBasedCountOnlyRequest req);
	long countTagBasedCountOnly(TagBasedCountOnlyRequest req);

	/**
	 * Tag-Based Score 일 때 total_score 만 반환
	 */
	List<AnimeRecommendDto2> selectTagBasedScoreOnly(TagBasedScoreOnlyRequest req);
	long countTagBasedScoreOnly(TagBasedScoreOnlyRequest req);

	List<TagScoreDto> selectTagScoresForUser(
		@Param(value = "userId") Long userId,
		@Param(value = "animeIds") List<Long> animeIds);
}
