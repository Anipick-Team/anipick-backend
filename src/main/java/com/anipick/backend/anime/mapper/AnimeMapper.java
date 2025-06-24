package com.anipick.backend.anime.mapper;

import java.util.List;

import com.anipick.backend.anime.domain.Anime;
import com.anipick.backend.anime.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AnimeMapper {
	List<AnimeIdTitleImgItemDto> selectUpcomingSeasonAnimes(RangeDateRequestDto rangeDateRequestDto);

	long countComingSoon(ComingSoonRequestDto comingSoonRequestDto);

	List<ComingSoonItemBasicDto> selectComingSoonLatestAnimes(ComingSoonRequestDto comingSoonRequestDto);

	List<ComingSoonItemPopularityDto> selectComingSoonPopularityAnimes(ComingSoonRequestDto comingSoonRequestDto);

	List<ComingSoonItemBasicDto> selectComingSoonStartDateAnimes(ComingSoonRequestDto comingSoonRequestDto);

	Anime selectAnimeByAnimeId(@Param("animeId") Long animeId);

	void updatePlusReviewCount(@Param("animeId") Long animeId);

	void updateMinusReviewCount(@Param("animeId") Long animeId);

	void updateReviewAverageScore(
			@Param("animeId") Long animeId,
			@Param("reviewAverageScore") Double reviewAverageScore
	);
}
