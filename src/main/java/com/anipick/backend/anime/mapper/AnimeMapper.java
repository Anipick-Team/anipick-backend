package com.anipick.backend.anime.mapper;

import java.util.List;

import com.anipick.backend.anime.service.dto.*;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnimeMapper {
	List<AnimeIdTitleImgItemDto> selectUpcomingSeasonAnimes(RangeDateRequestDto rangeDateRequestDto);

	long countComingSoon(ComingSoonRequestDto comingSoonRequestDto);

	List<ComingSoonItemBasicDto> selectComingSoonLatestAnimes(ComingSoonRequestDto comingSoonRequestDto);

	List<ComingSoonItemPopularityDto> selectComingSoonPopularityAnimes(ComingSoonRequestDto comingSoonRequestDto);

	List<ComingSoonItemBasicDto> selectComingSoonStartDateAnimes(ComingSoonRequestDto comingSoonRequestDto);
}
