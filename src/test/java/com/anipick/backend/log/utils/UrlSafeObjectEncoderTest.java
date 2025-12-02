package com.anipick.backend.log.utils;

import com.anipick.backend.log.domain.Area;
import com.anipick.backend.log.domain.DefaultDataBody;
import com.anipick.backend.log.domain.Page;
import com.anipick.backend.log.domain.UserActionLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UrlSafeObjectEncoder 테스트")
class UrlSafeObjectEncoderTest {

    @Test
    @DisplayName("UserActionLog 객체를 URL 안전한 Base64로 인코딩할 수 있다")
    void encodeURL_UserActionLog_Success() {
        // given
        DefaultDataBody dataBody = DefaultDataBody.createAnimeData(1, 1);
        UserActionLog userActionLog = UserActionLog.createClickLog(Page.SEARCH, Area.ITEM, dataBody);

        // when
        String encoded = UrlSafeObjectEncoder.encodeURL(userActionLog);

        // then
        assertThat(encoded).isNotNull();
        assertThat(encoded).isNotEmpty();
        assertThat(encoded).doesNotContain("+");
        assertThat(encoded).doesNotContain("/");
        assertThat(encoded).doesNotContain("=");
    }

    @Test
    @DisplayName("UserActionSearchLog 객체를 URL 안전한 Base64로 인코딩할 수 있다")
    void encodeURL_UserActionSearchLog_Success() {
        // given
        DefaultDataBody dataBody = DefaultDataBody.createAnimeData(1, 1);
        UserActionLog userActionLog = UserActionLog.createClickSearchLog(Page.SEARCH, Area.ITEM, dataBody, "검색키워드");

        // when
        String encoded = UrlSafeObjectEncoder.encodeURL(userActionLog);

        // then
        assertThat(encoded).isNotNull();
        assertThat(encoded).isNotEmpty();
        assertThat(encoded).doesNotContain("+");
        assertThat(encoded).doesNotContain("/");
        assertThat(encoded).doesNotContain("=");
    }

    @Test
    @DisplayName("null 객체를 인코딩할 때 예외가 발생한다")
    void encodeURL_NullObject_ThrowsException() {
        // given & when & then
        assertThatThrownBy(() -> UrlSafeObjectEncoder.encodeURL(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("순환 참조가 있는 객체를 인코딩할 때 예외가 발생한다")
    void encodeURL_CircularReference_ThrowsException() {
        // given
        CircularReferenceObject circularObject = new CircularReferenceObject();
        circularObject.setSelf(circularObject);

        // when & then
        assertThatThrownBy(() -> UrlSafeObjectEncoder.encodeURL(circularObject))
                .isInstanceOf(RuntimeException.class);
    }

    private static class CircularReferenceObject {
        private CircularReferenceObject self;

        public void setSelf(CircularReferenceObject self) {
            this.self = self;
        }

        public CircularReferenceObject getSelf() {
            return self;
        }
    }
}