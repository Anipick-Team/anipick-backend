package com.anipick.backend.review.dto;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class ReviewReportMessageRequest {
    private String message;

    public void validate() {
        if (!StringUtils.hasText(message)) {
            throw new CustomException(ErrorCode.REPORT_MESSAGE_EMPTY);
        }
    }
}
