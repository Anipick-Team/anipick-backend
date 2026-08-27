package com.anipick.backend.community.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommunityPostLikeMapper {

    Boolean selectUserLikePost(
            @Param("userId") Long userId,
            @Param("postId") Long postId
    );

    void insertLikePost(
            @Param("userId") Long userId,
            @Param("postId") Long postId
    );

    void deleteLikePost(
            @Param("userId") Long userId,
            @Param("postId") Long postId
    );
}
