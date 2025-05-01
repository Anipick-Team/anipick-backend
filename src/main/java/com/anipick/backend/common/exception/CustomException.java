package com.anipick.backend.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getErrorReason());
		this.errorCode = errorCode;
	}
}
