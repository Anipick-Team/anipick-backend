package com.anipick.backend.anime.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.anipick.backend.anime.service.dto.AnimeIdTitleImgItemDto;
import com.anipick.backend.anime.service.dto.RangeDateRequestDto;

@Mapper
public interface AnimeMapper {
	List<AnimeIdTitleImgItemDto> selectUpcomingSeasonAnimes(RangeDateRequestDto rangeDateRequestDto);
}
