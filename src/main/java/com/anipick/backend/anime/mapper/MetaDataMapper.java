package com.anipick.backend.anime.mapper;

import com.anipick.backend.anime.dto.GenreDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MetaDataMapper {
    List<GenreDto> selectAllGenres();
}
