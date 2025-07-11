package com.anipick.backend.anime.mapper;

import com.anipick.backend.anime.dto.GenreDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MetaDataMapper {
    List<GenreDto> selectAllGenres();

    String selectGenresById(@Param(value = "genresId") Long genres);
}
