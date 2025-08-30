package com.anipick.backend.search.mapper;

import com.anipick.backend.anime.dto.AnimeAllTitleImgDto;
import com.anipick.backend.anime.dto.StudioAllNameItemDto;
import com.anipick.backend.search.dto.PersonAllNameItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

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

	List<PersonAllNameItemDto> selectSearchPersons(
		@Param(value = "query") String query,
		@Param(value = "lastId") Long lastId,
		@Param(value = "size") Long size
	);

	long countSearchStudio(@Param(value = "query") String query);

	List<StudioAllNameItemDto> selectSearchStudios(
		@Param(value = "query") String query,
		@Param(value = "lastId") Long lastId,
		@Param(value = "size") Long size
	);
}
