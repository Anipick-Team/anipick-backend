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
    DefaultDataBody dataBody = DefaultDataBody.createAnimeData("애니메이션 제목", 1);
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
    assertThat(decodedLog.getDataBody().getContent()).isEqualTo(originalLog.getDataBody().getContent());
    assertThat(decodedLog.getDataBody().getPosition()).isEqualTo(originalLog.getDataBody().getPosition());
  }

  @Test
  @DisplayName("UserActionLog의 IMPRESSION 액션을 인코딩 후 디코딩하면 원본과 동일한 객체가 복원된다")
  void encodeAndDecode_UserActionLogImpression_ReturnsOriginalObject() {
    // given
    DefaultDataBody dataBody = DefaultDataBody.createAnimeData("다른 애니메이션", 5);
    UserActionLog originalLog = UserActionLog.createImpressionLog(Page.SEARCH, Area.ITEM, dataBody);

    // when
    String encoded = UrlSafeObjectEncoder.encodeURL(originalLog);
    UserActionLog decodedLog = UrlSafeObjectDecoder.decodeURL(encoded, UserActionLog.class);

    // then
    assertThat(decodedLog.getAction()).isEqualTo(UserActionLog.Action.IMPRESSION);
    assertThat(decodedLog.getDataBody().getContent()).isEqualTo("다른 애니메이션");
    assertThat(decodedLog.getDataBody().getPosition()).isEqualTo(5);
  }
}