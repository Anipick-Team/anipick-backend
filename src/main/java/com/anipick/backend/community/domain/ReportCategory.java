package com.anipick.backend.community.domain;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;

/**
 * community_report.report_category 컬럼 값. DB 에는 name() 그대로 저장된다.
 * - ABUSE: 욕설/비하/혐오 표현
 * - PRIVACY: 개인정보 노출
 * - SPAM: 도배/스팸/광고성 내용
 * - ILLEGAL: 불법/유해/부적절한 내용
 * - ETC: 기타 운영정책 위반
 */
public enum ReportCategory {
    ABUSE,
    PRIVACY,
    SPAM,
    ILLEGAL,
    ETC;

    public static ReportCategory of(String value) {
        if (value == null) {
            throw new CustomException(ErrorCode.COMMUNITY_REPORT_CATEGORY_INVALID);
        }
        return switch (value) {
            case "ABUSE" -> ABUSE;
            case "PRIVACY" -> PRIVACY;
            case "SPAM" -> SPAM;
            case "ILLEGAL" -> ILLEGAL;
            case "ETC" -> ETC;
            default -> throw new CustomException(ErrorCode.COMMUNITY_REPORT_CATEGORY_INVALID);
        };
    }
}
