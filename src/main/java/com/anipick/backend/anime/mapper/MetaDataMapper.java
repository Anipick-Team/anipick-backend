package com.anipick.backend.anime.mapper;

import com.anipick.backend.anime.service.dto.GenreDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MetaDataMapper {
    List<GenreDto> selectAllGenres();
}
