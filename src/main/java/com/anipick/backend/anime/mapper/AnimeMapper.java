package com.anipick.backend.anime.mapper;

import java.util.List;

import com.anipick.backend.anime.domain.Anime;
import com.anipick.backend.anime.domain.AnimeCharacterRole;
import com.anipick.backend.anime.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AnimeMapper {
	List<AnimeAllTitleImgDto> selectUpcomingSeasonAnimes(RangeDateRequestDto rangeDateRequestDto);

	long countComingSoon(ComingSoonRequestDto comingSoonRequestDto);

	List<ComingSoonItemAllTitleDto> selectComingSoonLatestAnimes(ComingSoonRequestDto comingSoonRequestDto);

	List<ComingSoonItemPopularityAlltitleDto> selectComingSoonPopularityAnimes(ComingSoonRequestDto comingSoonRequestDto);

	List<ComingSoonItemAllTitleDto> selectComingSoonStartDateAnimes(ComingSoonRequestDto comingSoonRequestDto);

	Anime selectAnimeByAnimeId(@Param("animeId") Long animeId);

	void updatePlusReviewCount(@Param("animeId") Long animeId);

	void updateMinusReviewCount(@Param("animeId") Long animeId);

	List<AnimeDetailInfoReviewsItemDto> selectAnimeDetailInfoReviews(AnimeDetailInfoReviewsRequestDto reviewsRequestDto);

	long selectAnimeReviewCount(@Param("animeId") Long animeId);
  
	AnimeDetailInfoItemDto selectAnimeInfoDetail(
			@Param(value = "animeId") Long animeId,
			@Param(value = "userId") Long userId
  );
  
	List<AnimeAllTitleImgDto> selectAnimeInfoRecommendationsByAnimeId(
			@Param("animeId") Long animeId,
			@Param("size") int size
  );

	Long selectSeriesGroupIdByAnimeId(@Param(value = "animeId") Long animeId);
  
  List<AnimeAllTitleDateDto> selectAnimeInfoSeriesByAnimeId(
			@Param(value = "seriesGroupId") Long seriesGroupId,
			@Param(value = "refAnimeId") Long animeId,
			@Param(value = "size") int size
  );
  
	List<AnimeCharacterActorItemDto> selectAnimeInfoCharacterActors(
			@Param("animeId") Long animeId,
			@Param("size") int size
  );
  
	void updateReviewAverageScore(
			@Param("animeId") Long animeId,
			@Param("reviewAverageScore") Double reviewAverageScore
	);

	void updateReviewAverageScoresByAnimeIds(@Param(value = "ids") List<Long> ids);

	List<AnimeCharacterActorResultDto> selectAnimeCharacterActors(
        @Param(value = "animeId") Long animeId,
        @Param(value = "lastId") Long lastId,
        @Param(value = "lastValue") AnimeCharacterRole lastValue,
        @Param(value = "size") int size
    );

	List<AnimeAllTitleImgDto> selectRecommendationsByAnimeId(
			@Param(value = "animeId") Long animeId,
			@Param(value = "lastId") Long lastId,
			@Param(value = "size") int size
	);

	long countSeriesAnime(
			@Param(value = "seriesGroupId") Long seriesGroupId,
			@Param(value = "refAnimeId") Long animeId
	);

	List<AnimeAllTitleDateDto> selectSeriesByAnimeId(
			@Param(value = "seriesGroupId") Long seriesGroupId,
			@Param(value = "refAnimeId") Long animeId,
			@Param(value = "lastId") Long lastId,
			@Param(value = "size") int size
	);

	AnimeMyReviewResultDto selectAnimeMyReview(
		@Param(value = "animeId") Long animeId,
		@Param(value = "userId") Long userId
	);
}
