package com.anipick.backend.version.mapper;

import com.anipick.backend.version.dto.NoticeUpdateItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserAppVersionMapper {
    Boolean existsRequiredUpdate(
            @Param("major") Integer major,
            @Param("minor") Integer minor,
            @Param("patch") Integer patch,
            @Param("platform") String platform
    );

    NoticeUpdateItemDto findVersion(@Param(value = "platform") String platform);
}
