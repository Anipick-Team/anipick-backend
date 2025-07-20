package com.anipick.backend.review.service;

import com.anipick.backend.review.dto.RecentReviewItemDto;
import com.anipick.backend.review.dto.RecentReviewPageDto;
import com.anipick.backend.review.mapper.RecentReviewMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private RecentReviewMapper mapper;

    @InjectMocks
    private ReviewService service;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 42L;
    }

    @Test
    @DisplayName("첫 페이지 정상 조회 - lastId null 일 경우")
    void firstPageSuccess() {
        // given
        given(mapper.countRecentReviews(userId)).willReturn(50L);

        RecentReviewItemDto sampleDto = new RecentReviewItemDto(
                10L, 123L, "타이틀", "url",
                4.5, "내용43254", "345efew", "imgUrl",
                "2025-05-03 17:16:46", 3L, true, false
        );
        given(mapper.selectRecentReviews(userId, null, 20))
                .willReturn(List.of(sampleDto));

        // when
        RecentReviewPageDto result = service.getRecentReviews(userId, null, 20);

        // then
        assertThat(result.getCount()).isEqualTo(50L);
        assertThat(result.getCursor().getLastId()).isEqualTo(10L);
        assertThat(result.getReviews()).hasSize(1);
        then(mapper).should().selectRecentReviews(userId, null, 20);
    }
}
