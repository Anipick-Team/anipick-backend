package com.anipick.backend.search.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.anipick.backend.anime.dto.AnimeItemDto;

@Mapper
public interface SearchMapper {
	long countSearchAnime(@Param(value = "queryPattern") String queryPattern);

	List<AnimeItemDto> selectSearchAnimes(
		@Param(value = "queryPattern") String queryPattern,
		@Param(value = "lastId") Long lastId,
		@Param(value = "size") Long size
	);
}
