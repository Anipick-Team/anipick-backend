package com.anipick.backend.common.exception;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.anipick.backend.common.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// CustomException
	@ExceptionHandler(CustomException.class)
	public ApiResponse<?> handleBusiness(CustomException ex) {
		log.error("log info : ", ex);
		ErrorCode ec = ex.getErrorCode();
		return ApiResponse.error(ec);
	}

	// JSON 파싱 에러 등
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ApiResponse<?> handleBadRequest(HttpMessageNotReadableException ex) {
		log.error("log info {} : ", ex.getMessage());
		return ApiResponse.error(ErrorCode.BAD_REQUEST);
	}

	// 파일 업로드 용량 초과 (spring.servlet.multipart.max-file-size)
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ApiResponse<?> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
		log.error("log info {} : ", ex.getMessage());
		return ApiResponse.error(ErrorCode.IMAGE_SIZE_EXCEEDED);
	}

	// 그 외 모든 예외 (서버 오류)
	@ExceptionHandler(Exception.class)
	public ApiResponse<?> handleAll(Exception ex) {
		log.error("log info : ", ex);
		return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
	}
}
