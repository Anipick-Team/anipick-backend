package com.anipick.backend.search.mapper;

import java.time.LocalDate;
import java.util.List;

import com.anipick.backend.anime.dto.AnimeAllTitleImgDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.search.dto.PersonItemDto;
import com.anipick.backend.search.dto.StudioItemDto;

@Mapper
public interface SearchMapper {

	List<AnimeAllTitleImgDto> selectSearchWeekBestAnimes(@Param(value = "now") LocalDate now);

	long countSearchAnime(@Param(value = "query") String query);

	List<AnimeAllTitleImgDto> selectSearchAnimes(
		@Param(value = "query") String query,
		@Param(value = "lastId") Long lastId,
		@Param(value = "size") Long size
	);

	long countSearchPerson(@Param(value = "query") String query);

	List<PersonItemDto> selectSearchPersons(
		@Param(value = "query") String query,
		@Param(value = "lastId") Long lastId,
		@Param(value = "size") Long size
	);

	long countSearchStudio(@Param(value = "query") String query);

	List<StudioItemDto> selectSearchStudios(
		@Param(value = "query") String query,
		@Param(value = "lastId") Long lastId,
		@Param(value = "size") Long size
	);
}
