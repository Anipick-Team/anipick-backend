package com.anipick.backend.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BlockUserMapper {
    Boolean selectBlockUser(
            @Param(value = "userId") Long userId,
            @Param(value = "targetUserId") Long targetUserId
    );

    void insertBlockUser(
            @Param(value = "userId") Long userId,
            @Param(value = "targetUserId") Long targetUserId
    );
}
