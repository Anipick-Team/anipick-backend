package com.anipick.backend.community.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommunityCommentLikeMapper {

    Boolean selectUserLikeComment(
            @Param("userId") Long userId,
            @Param("commentId") Long commentId
    );

    void insertLikeComment(
            @Param("userId") Long userId,
            @Param("commentId") Long commentId
    );

    void deleteLikeComment(
            @Param("userId") Long userId,
            @Param("commentId") Long commentId
    );
}
