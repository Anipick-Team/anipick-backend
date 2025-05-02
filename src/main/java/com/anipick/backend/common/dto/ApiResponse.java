package com.anipick.backend.common.dto;

import com.anipick.backend.common.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
	private int code;
	private String value;
	private T result;
	private String errorReason;
	private String errorValue;

	// 성공 응답 (데이터 있는 경우)
	public static <T> ApiResponse<T> success(T result) {
		return new ApiResponse<>(200, "success", result, null, null);
	}

	// 성공 응답 (데이터 없는 경우)
	public static ApiResponse<Void> success() {
		return new ApiResponse<>(200, "success", null, null, null);
	}

	// 실패 응답
	public static <T> ApiResponse<T> error(ErrorCode ec) {
		return new ApiResponse<>(
			ec.getCode(),
			"false",
			null,
			ec.getErrorReason(),
			ec.getErrorValue()
		);
	}
}
