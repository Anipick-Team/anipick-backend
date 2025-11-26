package com.anipick.backend.version.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("버전 API 파라미터 유효성 테스트")
class VersionValidatorTest {

	@Test
	@DisplayName("정상적인 버전 형식이면 true를 반환한다")
	void validVersionFormat() {
		// given
		String version = "1.2.3";

		// when
		boolean result = VersionValidator.checkValidationVersion(version);

		// then
		assertTrue(result);
	}

	@Test
	@DisplayName("선행 0이 포함된 버전 형식은 false를 반환한다")
	void versionWithLeadingZero() {
		// given
		String version = "01.2.3";

		// when
		boolean result = VersionValidator.checkValidationVersion(version);

		// then
		assertFalse(result);
	}

	@Test
	@DisplayName("필수 3개 섹션(major.minor.patch)을 만족하지 않으면 false를 반환한다")
	void versionMissingSection() {
		// given
		String version = "1.2";

		// when
		boolean result = VersionValidator.checkValidationVersion(version);

		// then
		assertFalse(result);
	}

	@Test
	@DisplayName("섹션이 3개를 초과하면 false를 반환한다")
	void versionTooManySections() {
		// given
		String version = "1.2.3.4";

		// when
		boolean result = VersionValidator.checkValidationVersion(version);

		// then
		assertFalse(result);
	}

	@Test
	@DisplayName("숫자가 아닌 값이 포함되면 false를 반환한다")
	void versionContainsNonNumeric() {
		// given
		String version = "a.b.c";

		// when
		boolean result = VersionValidator.checkValidationVersion(version);

		// then
		assertFalse(result);
	}

	@Test
	@DisplayName("null 값이면 false를 반환한다")
	void nullVersion() {
		// given
		String version = null;

		// when
		boolean result = VersionValidator.checkValidationVersion(version);

		// then
		assertFalse(result);
	}
}
