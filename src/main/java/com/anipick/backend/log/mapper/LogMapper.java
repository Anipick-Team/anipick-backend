package com.anipick.backend.log.mapper;

import com.anipick.backend.log.dto.LogCreateRequestDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogMapper {
    void createLog(LogCreateRequestDto logCreateRequestDto);
}
