package com.anipick.backend.log.utils;

import com.anipick.backend.log.domain.Area;
import com.anipick.backend.log.domain.DefaultDataBody;
import com.anipick.backend.log.domain.Page;
import com.anipick.backend.log.domain.UserActionLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UrlSafeObjectEncoder와 UrlSafeObjectDecoder 통합 테스트")
class UrlSafeObjectEncoderDecoderIntegrationTest {

    @Test
    @DisplayName("UserActionLog를 인코딩 후 디코딩하면 원본과 동일한 객체가 복원된다")
    void encodeAndDecode_UserActionLog_ReturnsOriginalObject() {
        // given
        DefaultDataBody dataBody = DefaultDataBody.createAnimeData(1, 1);
        UserActionLog originalLog = UserActionLog.createClickLog(Page.SEARCH, Area.ITEM, dataBody);

        // when
        String encoded = UrlSafeObjectEncoder.encodeURL(originalLog);
        UserActionLog decodedLog = UrlSafeObjectDecoder.decodeURL(encoded, UserActionLog.class);

        // then
        assertThat(decodedLog).isNotNull();
        assertThat(decodedLog.getAction()).isEqualTo(originalLog.getAction());
        assertThat(decodedLog.getPage()).isEqualTo(originalLog.getPage());
        assertThat(decodedLog.getArea()).isEqualTo(originalLog.getArea());
        assertThat(decodedLog.getDataBody()).isNotNull();
        assertThat(decodedLog.getDataBody().getAnimeId()).isEqualTo(originalLog.getDataBody().getAnimeId());
        assertThat(decodedLog.getDataBody().getPosition()).isEqualTo(originalLog.getDataBody().getPosition());
    }

    @Test
    @DisplayName("UserActionSearchLog를 인코딩 후 디코딩하면 원본과 동일한 객체가 복원된다")
    void encodeAndDecode_UserActionSearchLog_ReturnsOriginalObject() {
        // given
        DefaultDataBody dataBody = DefaultDataBody.createAnimeData(1, 1);
        UserActionLog originalLog = UserActionLog.createClickSearchLog(Page.SEARCH, Area.ITEM, dataBody, "검색키워드");

        // when
        String encoded = UrlSafeObjectEncoder.encodeURL(originalLog);
        UserActionLog decodedLog = UrlSafeObjectDecoder.decodeURL(encoded, UserActionLog.class);

        // then
        assertThat(decodedLog).isNotNull();
        assertThat(decodedLog.getAction()).isEqualTo(originalLog.getAction());
        assertThat(decodedLog.getPage()).isEqualTo(originalLog.getPage());
        assertThat(decodedLog.getArea()).isEqualTo(originalLog.getArea());
        assertThat(decodedLog.getQuery()).isEqualTo(originalLog.getQuery());
        assertThat(decodedLog.getDataBody()).isNotNull();
        assertThat(decodedLog.getDataBody().getAnimeId()).isEqualTo(originalLog.getDataBody().getAnimeId());
        assertThat(decodedLog.getDataBody().getPosition()).isEqualTo(originalLog.getDataBody().getPosition());
    }

    @Test
    @DisplayName("UserActionLog의 IMPRESSION 액션을 인코딩 후 디코딩하면 원본과 동일한 객체가 복원된다")
    void encodeAndDecode_UserActionLogImpression_ReturnsOriginalObject() {
        // given
        DefaultDataBody dataBody = DefaultDataBody.createAnimeData(1, 5);
        UserActionLog originalLog = UserActionLog.createImpressionLog(Page.SEARCH, Area.ITEM, dataBody);

        // when
        String encoded = UrlSafeObjectEncoder.encodeURL(originalLog);
        UserActionLog decodedLog = UrlSafeObjectDecoder.decodeURL(encoded, UserActionLog.class);

        // then
        assertThat(decodedLog.getAction()).isEqualTo(originalLog.getAction());
        assertThat(decodedLog.getDataBody().getAnimeId()).isEqualTo(1);
        assertThat(decodedLog.getDataBody().getPosition()).isEqualTo(5);
    }

    @Test
    @DisplayName("UserActionSearchLog의 IMPRESSION 액션을 인코딩 후 디코딩하면 원본과 동일한 객체가 복원된다")
    void encodeAndDecode_UserActionSearchLogImpression_ReturnsOriginalObject() {
        // given
        DefaultDataBody dataBody = DefaultDataBody.createAnimeData(1, 5);
        UserActionLog originalLog = UserActionLog.createImpressionSearchLog(Page.SEARCH, Area.ITEM, dataBody, "검색키워드");

        // when
        String encoded = UrlSafeObjectEncoder.encodeURL(originalLog);
        UserActionLog decodedLog = UrlSafeObjectDecoder.decodeURL(encoded, UserActionLog.class);

        // then
        assertThat(decodedLog.getAction()).isEqualTo(originalLog.getAction());
        assertThat(decodedLog.getQuery()).isEqualTo(originalLog.getQuery());
        assertThat(decodedLog.getDataBody().getAnimeId()).isEqualTo(1);
        assertThat(decodedLog.getDataBody().getPosition()).isEqualTo(5);
    }
}