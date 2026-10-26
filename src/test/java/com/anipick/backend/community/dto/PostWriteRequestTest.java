package com.anipick.backend.community.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DisplayName("게시글 작성/수정 요청 검증")
class PostWriteRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private <T> T parse(Map<String, Object> body, Class<T> type) throws Exception {
        return objectMapper.readValue(objectMapper.writeValueAsString(body), type);
    }

    private PostUpdateRequest updateRequest(Object content) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", "제목");
        body.put("content", content);
        return parse(body, PostUpdateRequest.class);
    }

    @Test
    @DisplayName("작성 요청 JSON은 seriesId와 공통 필드가 모두 바인딩된다")
    void createRequestBindsAllFields() throws Exception {
        PostCreateRequest sut = parse(Map.of(
                "seriesId", 10,
                "title", "제목",
                "content", "내용",
                "isSpoiler", true,
                "imageIds", List.of(1, 2)
        ), PostCreateRequest.class);

        sut.validate();

        assertThat(sut.getSeriesId()).isEqualTo(10L);
        assertThat(sut.getTitle()).isEqualTo("제목");
        assertThat(sut.getContent()).isEqualTo("내용");
        assertThat(sut.getSpoilerOrDefault()).isTrue();
        assertThat(sut.getImageIds()).containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("content 필드가 없으면 빈 문자열로 정규화된다")
    void missingContentBecomesEmpty() throws Exception {
        PostCreateRequest sut = parse(Map.of("seriesId", 1, "title", "제목"), PostCreateRequest.class);

        sut.validate();

        assertThat(sut.getContent()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "\n\t "})
    @DisplayName("빈 문자열·공백만 있는 content는 빈 문자열로 정규화된다")
    void blankContentBecomesEmpty(String content) throws Exception {
        PostUpdateRequest sut = updateRequest(content);

        sut.validate();

        assertThat(sut.getContent()).isEmpty();
    }

    @Test
    @DisplayName("null content는 빈 문자열로 정규화된다")
    void nullContentBecomesEmpty() throws Exception {
        PostUpdateRequest sut = updateRequest(null);

        sut.validate();

        assertThat(sut.getContent()).isEmpty();
    }

    @Test
    @DisplayName("일반 content는 앞뒤 공백을 포함해 그대로 유지된다")
    void normalContentIsKept() throws Exception {
        PostUpdateRequest sut = updateRequest("  안녕  ");

        sut.validate();

        assertThat(sut.getContent()).isEqualTo("  안녕  ");
    }

    @Test
    @DisplayName("content가 1000자면 통과한다")
    void contentOf1000Passes() throws Exception {
        PostUpdateRequest sut = updateRequest("a".repeat(1000));

        sut.validate();

        assertThat(sut.getContent()).hasSize(1000);
    }

    @Test
    @DisplayName("content가 1001자면 COMMUNITY_POST_CONTENT_LENGTH_INVALID 예외가 발생한다")
    void contentOf1001Fails() throws Exception {
        PostUpdateRequest sut = updateRequest("a".repeat(1001));

        assertThatThrownBy(sut::validate)
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.COMMUNITY_POST_CONTENT_LENGTH_INVALID);
    }

    @Test
    @DisplayName("공백만 1000자를 넘게 보내면 빈 문자열로 정규화되어 통과한다")
    void longBlankContentBecomesEmpty() throws Exception {
        PostUpdateRequest sut = updateRequest(" ".repeat(1500));

        sut.validate();

        assertThat(sut.getContent()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("제목이 비어 있으면 COMMUNITY_POST_TITLE_LENGTH_INVALID 예외가 발생한다")
    void blankTitleFails(String title) throws Exception {
        PostCreateRequest sut = parse(Map.of("seriesId", 1, "title", title, "content", "내용"), PostCreateRequest.class);

        assertThatThrownBy(sut::validate)
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.COMMUNITY_POST_TITLE_LENGTH_INVALID);
    }

    @Test
    @DisplayName("제목이 51자면 COMMUNITY_POST_TITLE_LENGTH_INVALID 예외가 발생한다")
    void titleOf51Fails() throws Exception {
        PostCreateRequest sut = parse(Map.of("seriesId", 1, "title", "a".repeat(51)), PostCreateRequest.class);

        assertThatThrownBy(sut::validate)
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.COMMUNITY_POST_TITLE_LENGTH_INVALID);
    }

    @Test
    @DisplayName("이미지가 6장이면 COMMUNITY_POST_IMAGE_COUNT_EXCEEDED 예외가 발생한다")
    void sixImagesFail() throws Exception {
        PostCreateRequest sut = parse(Map.of(
                "seriesId", 1, "title", "제목", "imageIds", List.of(1, 2, 3, 4, 5, 6)
        ), PostCreateRequest.class);

        assertThatThrownBy(sut::validate)
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.COMMUNITY_POST_IMAGE_COUNT_EXCEEDED);
    }

    @Test
    @DisplayName("isSpoiler가 없으면 false로 처리된다")
    void missingSpoilerIsFalse() throws Exception {
        PostCreateRequest sut = parse(Map.of("seriesId", 1, "title", "제목"), PostCreateRequest.class);

        assertThat(sut.getSpoilerOrDefault()).isFalse();
    }

    @Test
    @DisplayName("MyBatis가 수정 요청의 #{request.*} 프로퍼티를 정규화된 값으로 읽는다")
    void myBatisReadsNormalizedProperties() throws Exception {
        PostUpdateRequest sut = updateRequest(null);
        sut.validate();

        // CommunityPostCommandMapper.xml updatePost 가 참조하는 프로퍼티
        MetaObject meta = SystemMetaObject.forObject(sut);

        assertThat(meta.getValue("title")).isEqualTo("제목");
        assertThat(meta.getValue("content")).isEqualTo("");
        assertThat(meta.getValue("isSpoiler")).isNull();
    }
}
