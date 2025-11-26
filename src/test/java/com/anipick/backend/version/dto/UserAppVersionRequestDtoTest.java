package com.anipick.backend.version.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.oauth.domain.Platform;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("버전 API가 실패할 때 테스트")
class UserAppVersionRequestDtoTest {

	@Test
	@DisplayName("유효하지 않은 버전 형식이 들어오면 CustomException을 던진다")
	void invalidVersionThrowsException() {
		// given
		String invalidVersion = "01.2.3";
		Platform platform = Platform.ANDROID;

		// when
		CustomException exception = assertThrows(
			CustomException.class,
			() -> UserAppVersionRequestDto.from(invalidVersion, platform)
		);

		// then
		assertEquals("BAD_REQUEST", exception.getErrorCode().name());
	}

	@Test
	@DisplayName("섹션이 부족한 버전 형식은 CustomException을 던진다")
	void missingSectionThrowsException() {
		// given
		String invalidVersion = "1.2";
		Platform platform = Platform.IOS;

		// when && then
		assertThrows(
			CustomException.class,
			() -> UserAppVersionRequestDto.from(invalidVersion, platform)
		);
	}

	@Test
	@DisplayName("null 버전 문자열은 CustomException을 던진다")
	void nullVersionThrowsException() {
		// given
		String invalidVersion = null;
		Platform platform = Platform.IOS;

		// when && then
		assertThrows(
			CustomException.class,
			() -> UserAppVersionRequestDto.from(invalidVersion, platform)
		);
	}
}
