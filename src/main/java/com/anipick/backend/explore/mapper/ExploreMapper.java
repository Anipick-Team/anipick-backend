package com.anipick.backend.explore.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.anipick.backend.explore.dto.ExploreItemDto;

@Mapper
public interface ExploreMapper {
	long countExplored(
		@Param(value = "year") Integer year,
		@Param(value = "season") Integer season,
		@Param(value = "genres") List<Long> genres,
		@Param(value = "genresSize") int genresSize,
		@Param(value = "types") List<String> types,
		@Param(value = "typeConvertSize") Integer typeConvertSize,
		@Param(value = "genreOp") String genreOp,
		@Param(value = "type") String type
	);

	List<ExploreItemDto> selectExplored(
		@Param(value = "year") Integer year,
		@Param(value = "season") Integer season,
		@Param(value = "genres") List<Long> genres,
		@Param(value = "genresSize") int genresSize,
		@Param(value = "types") List<String> types,
		@Param(value = "typeConvertSize") Integer typeConvertSize,
		@Param(value = "genreOp") String genreOp,
		@Param(value = "type") String type,
		@Param(value = "sort") String sort,
		@Param(value = "orderByQuery") String orderByQuery,
		@Param(value = "lastId") Long lastId,
		@Param(value = "lastValue") Integer lastValue,
		@Param(value = "size") int size
	);
}
