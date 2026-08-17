package com.anipick.backend.community.mapper;

import com.anipick.backend.community.domain.ReportCategory;
import com.anipick.backend.community.domain.ReportTargetType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommunityReportMapper {

    void insertReport(
            @Param("reporterUserId") Long reporterUserId,
            @Param("targetType") ReportTargetType targetType,
            @Param("targetId") Long targetId,
            @Param("reportCategory") ReportCategory reportCategory
    );
}
