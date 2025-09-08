package com.anipick.backend.anime.mapper;

import com.anipick.backend.anime.dto.GenreDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GenreMapper {
    List<GenreDto> selectGenresByAnimeId(@Param(value = "animeId") Long animeId);
    Long findGenreIdByGenreName(@Param("genreName") String genreName);
}
