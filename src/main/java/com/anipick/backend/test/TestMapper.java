package com.anipick.backend.test;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestMapper {
    List<TestResponseDto> findReviews(@Param(value = "userId") long userId);
}
