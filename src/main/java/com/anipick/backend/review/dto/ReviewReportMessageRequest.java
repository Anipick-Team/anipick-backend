package com.anipick.backend.review.dto;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ReviewReportMessageRequest {
    private String message;

    public void validate() {
        if (message == null || message.trim().isEmpty()) {
            throw new CustomException(ErrorCode.REPORT_MESSAGE_EMPTY);
        }
    }
}
