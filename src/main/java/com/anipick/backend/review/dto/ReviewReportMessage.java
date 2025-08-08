package com.anipick.backend.review.dto;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewReportMessage {
    private String message;

    public void validate() {
        if (message == null || message.trim().isEmpty()) {
            throw new CustomException(ErrorCode.REPORT_MESSAGE_EMPTY);
        }
    }
}
