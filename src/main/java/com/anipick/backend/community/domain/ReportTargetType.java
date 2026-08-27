package com.anipick.backend.community.domain;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;

/**
 * community_report.target_type 컬럼 값. DB 에는 name() 그대로 저장된다.
 */
public enum ReportTargetType {
    POST,
    COMMENT;

    public static ReportTargetType of(String value) {
        if (value == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        return switch (value) {
            case "POST" -> POST;
            case "COMMENT" -> COMMENT;
            default -> throw new CustomException(ErrorCode.BAD_REQUEST);
        };
    }
}
