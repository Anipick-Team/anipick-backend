package com.anipick.backend.log.utils;

import com.anipick.backend.log.domain.Area;
import com.anipick.backend.log.domain.DefaultDataBody;
import com.anipick.backend.log.domain.Page;
import com.anipick.backend.log.domain.UserActionLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UrlSafeObjectDecoder 테스트")
class UrlSafeObjectDecoderTest {

    @Test
    @DisplayName("URL 안전한 Base64로 인코딩된 UserActionLog를 디코딩할 수 있다")
    void decodeURL_UserActionLog_Success() {
        // given
        DefaultDataBody dataBody = DefaultDataBody.createAnimeData("애니메이션 제목", 1);
        UserActionLog originalLog = UserActionLog.createClickLog(Page.SEARCH, Area.ITEM, dataBody);
        String encoded = UrlSafeObjectEncoder.encodeURL(originalLog);

        // when
        UserActionLog decodedLog = UrlSafeObjectDecoder.decodeURL(encoded, UserActionLog.class);

        // then
        assertThat(decodedLog.getAction()).isEqualTo(originalLog.getAction());
        assertThat(decodedLog.getPage()).isEqualTo(originalLog.getPage());
        assertThat(decodedLog.getArea()).isEqualTo(originalLog.getArea());
        assertThat(decodedLog.getDataBody().getContent()).isEqualTo(originalLog.getDataBody().getContent());
        assertThat(decodedLog.getDataBody().getPosition()).isEqualTo(originalLog.getDataBody().getPosition());
    }

    @Test
    @DisplayName("URL 안전한 Base64로 인코딩된 UserActionSearchLog를 디코딩할 수 있다")
    void decodeURL_UserActionSearchLog_Success() {
        // given
        DefaultDataBody dataBody = DefaultDataBody.createAnimeData("애니메이션 제목", 1);
        UserActionLog originalLog = UserActionLog.createClickSearchLog(Page.SEARCH, Area.ITEM, dataBody, "검색키워드");
        String encoded = UrlSafeObjectEncoder.encodeURL(originalLog);

        // when
        UserActionLog decodedLog = UrlSafeObjectDecoder.decodeURL(encoded, UserActionLog.class);

        // then
        assertThat(decodedLog.getAction()).isEqualTo(originalLog.getAction());
        assertThat(decodedLog.getPage()).isEqualTo(originalLog.getPage());
        assertThat(decodedLog.getArea()).isEqualTo(originalLog.getArea());
        assertThat(decodedLog.getDataBody().getContent()).isEqualTo(originalLog.getDataBody().getContent());
        assertThat(decodedLog.getDataBody().getPosition()).isEqualTo(originalLog.getDataBody().getPosition());
    }

    @Test
    @DisplayName("잘못된 Base64 문자열을 디코딩할 때 예외가 발생한다")
    void decodeURL_InvalidBase64_ThrowsException() {
        // given
        String invalidBase64 = "invalid-base64-string!@#";

        // when & then
        assertThatThrownBy(() -> UrlSafeObjectDecoder.decodeURL(invalidBase64, UserActionLog.class))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("null 문자열을 디코딩할 때 예외가 발생한다")
    void decodeURL_NullString_ThrowsException() {

        // given & when & then
        assertThatThrownBy(() -> UrlSafeObjectDecoder.decodeURL(null, UserActionLog.class))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("빈 문자열을 디코딩할 때 예외가 발생한다")
    void decodeURL_EmptyString_ThrowsException() {
        // given
        String emptyString = "";

        // when & then
        assertThatThrownBy(() -> UrlSafeObjectDecoder.decodeURL(emptyString, UserActionLog.class))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("잘못된 JSON 형식의 Base64 문자열을 디코딩할 때 예외가 발생한다")
    void decodeURL_InvalidJson_ThrowsException() {
        // given
        String invalidJson = "eyJpbnZhbGlkIjogImpzb24ifQ=="; // {"invalid": "json"}

        // when & then
        assertThatThrownBy(() -> UrlSafeObjectDecoder.decodeURL(invalidJson, UserActionLog.class))
                .isInstanceOf(RuntimeException.class);
    }
}