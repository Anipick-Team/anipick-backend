package com.anipick.backend.community.dto;

import lombok.Getter;

/**
 * POST /api/community/reports 요청. 문자열 → enum 변환은 서비스에서 수행.
 */
@Getter
public class ReportCreateRequest {
    private String targetType;
    private Long targetId;
    private String reportCategory;
}
