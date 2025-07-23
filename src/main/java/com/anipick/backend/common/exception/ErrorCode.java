package com.anipick.backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	/**
	 * User
	 */
	LOGIN_FAIL(101, "로그인 실패", "이메일이나 비밀번호를 확인해 주세요."),
	EMAIL_NOT_PROVIDED(102, "이메일 미 입력", "이메일 주소를 입력해 주세요."),
	EMAIL_INVALID_FORMAT(103, "이메일 형식 X", "올바른 이메일 형식이 아닙니다."),
	LOGIN_EMAIL_NOT_FOUND(104, "이메일 찾을 수 없음", null),
	PASSWORD_NOT_PROVIDED(105, "비밀번호 미 입력", "비밀번호를 입력해 주세요."),
	LOGIN_PASSWORD_MISMATCH(106, "비밀번호 불일치", null),
	PASSWORD_CHANGE_OLD_MISMATCH(107, "비밀번호 변경 시 현재 비밀번호 불일치", "현재 비밀번호가 일치하지 않습니다. 다시 입력해 주세요."),
	PASSWORD_CONFIRM_MISMATCH(108, "비밀번호 확인 불일치", "비밀번호가 일치하지 않습니다."),
	EMAIL_ALREADY_REGISTERED(109, "이미 가입된 이메일", "이미 존재하는 이메일입니다."),
	PASSWORD_WEAK(110, "비밀번호 취약", "8~16자의 영문 대/소문자, 숫자, 특수문자를 조합하여 입력해 주세요."),
	TERMS_NOT_AGREED(111, "이용약관 미 동의", "이용약관에 동의해 주세요."),
	ACCOUNT_NOT_FOUND_BY_EMAIL(112, "가입된 계정 없음", "가입된 계정이 없습니다. 이메일을 다시 확인해 주세요."),
	VERIFICATION_CODE_NOT_PROVIDED(113, "인증번호 미 입력", "인증번호를 입력해 주세요."),
	VERIFICATION_CODE_MISMATCH(114, "인증번호 불일치", "인증번호가 올바르지 않습니다."),
	VERIFICATION_CODE_EXPIRED(115, "인증번호 유효 시간 만료", "유효 시간이 만료되었습니다. 재발송 후 다시 시도해 주세요."),
	NICKNAME_INVALID_FORMAT(116, "닉네임 형식 X", "1~20자의 한글, 영문 대/소문자, 숫자, 특수문자를 조합하여 입력해 주세요."),
	NICKNAME_DUPLICATE(117, "닉네임 중복", "이미 사용 중인 닉네임입니다."),
	NICKNAME_NOT_PROVIDED(118, "닉네임 미 입력", "닉네임을 입력해 주세요."),
	REQUESTED_TOKEN_INVALID(119, "토큰 값이 유효 X", "토큰의 요청 값이 유효하지 않습니다."),
	PASSWORD_INVALID_FORMAT(120, "비밀번호 형식 X", "8~16자의 영문 대/소문자, 숫자, 특수문자를 조합하여 입력해주세요."),
	EXPIRED_TOKEN(121, "토큰 만료", "토큰이 만료되었습니다."),
	EMAIL_SNS_ACCOUNT_EXISTS(122, "SNS로 간편 가입된 계정", "SNS로 간편 가입된 계정입니다."),
	EMAIL_AUTH_FAILED(123, "SMTP 인증 실패", "SMTP 인증 실패하였습니다."),
	EMAIL_PARSE_ERROR(124, "메시지 파싱 오류", "메시지 파싱 오류가 발생하였습니다."),
	EMAIL_PREPARE_ERROR(125, "메시지 준비 오류", "메시지 준비 중 오류가 발생하였습니다."),
	EMAIL_SEND_FAILED(126, "메일 전송 실패", "메일 전송이 실패하였습니다."),
	EMAIL_GENERIC_ERROR(127, "일반적인 메일 오류", "일반적인 메일 오류가 발생하였습니다."),
	ALREADY_USER_ANIME_OF_STATUS(128, "유저 시청상태 데이터가 이미 존재", null),
	USER_ANIME_OF_STATUS_DATA_NOT_FOUND(129, "유저 시청상태 데이터 찾을 수 없음", null),
	/**
	 * Review
	 */
	REVIEW_CONTENT_NOT_PROVIDED(301, "리뷰 내용 미작성", "내용을 입력해 주세요."),
	REVIEW_NOT_FOUND(302, "리뷰 찾을 수 없음", null),
	REVIEW_ALREADY_EXISTS(303, "이미 평가가 존재", null),
	/**
	 * Validation & Request Errors
	 */
	VALIDATION_FAIL(400, "서버 측 입력 값 유효 X", "입력 값이 올바르지 않습니다."),
	BAD_REQUEST(400, "요청 형식의 오류", "요청 형식이 잘못되었습니다."),
	/**
	 * Server Errors
	 */
	INTERNAL_SERVER_ERROR(500, "서버 오류", "서버에 문제가 발생했습니다."),
	/**
	 * Anime Explore
	 */
	EMPTY_YEAR(601, "년도를 입력하지 않음", "년도를 입력해 주세요."),
	/**
	 * Search
	 */
	EMPTY_KEYWORD(701, "검색 키워드를 입력하지 않음", "검색 키워드를 입력해 주세요."),
	/**
	 * LIKE
	 */
	LIKE_DATA_NOT_FOUND(801, "좋아요 데이터 찾을 수 없음", null),
	ALREADY_LIKE_DATA(802, "좋아요 데이터가 이미 존재", null);

	private final int code;
	private final String errorReason;
	private final String errorValue;
}
