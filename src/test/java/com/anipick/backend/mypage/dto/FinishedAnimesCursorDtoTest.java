package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.domain.SortOption;
import com.anipick.backend.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinishedAnimesCursorDtoTest {

    @Test
    @DisplayName("다 본 애니에서 지원하지 않는 정렬값은 첫 페이지에서도 400 에러")
    void of_notAllowedSort() {
        assertThatThrownBy(() -> FinishedAnimesCursorDto.of(SortOption.LIKES, null, null))
                .isInstanceOf(CustomException.class);
        assertThatThrownBy(() -> FinishedAnimesCursorDto.of(SortOption.POPULARITY, 10L, "1"))
                .isInstanceOf(CustomException.class);
        assertThat(FinishedAnimesCursorDto.of(SortOption.RATING_ASC, null, null).getSortOption()).isEqualTo("ratingAsc");
    }

    @Test
    @DisplayName("lastId가 없으면 첫 페이지로 보고 lastValue는 무시한다")
    void of_firstPage() {
        FinishedAnimesCursorDto sut = FinishedAnimesCursorDto.of(SortOption.LATEST, null, "invalid");

        assertThat(sut.getLastId()).isNull();
        assertThat(sut.getLastSortedAt()).isNull();
        assertThat(sut.getSortOption()).isEqualTo("latest");
    }

    @Test
    @DisplayName("최신순 lastValue를 평가한 시간으로 해석한다")
    void of_latest() {
        FinishedAnimesCursorDto sut = FinishedAnimesCursorDto.of(SortOption.LATEST, 21L, "2025-11-08 14:23:11");

        assertThat(sut.getLastSortedAt()).isEqualTo(LocalDateTime.of(2025, 11, 8, 14, 23, 11));
    }

    @Test
    @DisplayName("최신순에서 lastId만 있고 lastValue가 없거나 형식이 틀리면 400 에러")
    void of_latest_invalid() {
        assertThatThrownBy(() -> FinishedAnimesCursorDto.of(SortOption.LATEST, 21L, null))
                .isInstanceOf(CustomException.class);
        assertThatThrownBy(() -> FinishedAnimesCursorDto.of(SortOption.LATEST, 21L, "2025-11-08"))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("방영일 순에서 lastValue가 없으면 방영 미정 구간으로 본다")
    void of_startDate() {
        FinishedAnimesCursorDto withDate = FinishedAnimesCursorDto.of(SortOption.START_DATE, 22L, "2013-04-07");
        FinishedAnimesCursorDto noDate = FinishedAnimesCursorDto.of(SortOption.START_DATE, 7L, null);

        assertThat(withDate.getLastStartDate()).isEqualTo(LocalDate.of(2013, 4, 7));
        assertThat(noDate.getLastId()).isEqualTo(7L);
        assertThat(noDate.getLastStartDate()).isNull();
    }

    @Test
    @DisplayName("평가 순 lastValue를 평점과 시간으로 나누고, 평점 자리가 비면 평가 없는 구간으로 본다")
    void of_rating() {
        FinishedAnimesCursorDto rated = FinishedAnimesCursorDto.of(SortOption.RATING_DESC, 18L, "4.0,2025-11-01 09:12:45");
        FinishedAnimesCursorDto unrated = FinishedAnimesCursorDto.of(SortOption.RATING_ASC, 21L, ",2025-10-20 18:03:10");

        assertThat(rated.getLastRating()).isEqualTo(4.0);
        assertThat(rated.getLastSortedAt()).isEqualTo(LocalDateTime.of(2025, 11, 1, 9, 12, 45));
        assertThat(unrated.getLastRating()).isNull();
        assertThat(unrated.getLastSortedAt()).isEqualTo(LocalDateTime.of(2025, 10, 20, 18, 3, 10));
    }

    @Test
    @DisplayName("평가 순 lastValue 형식이 틀리면 400 에러")
    void of_rating_invalid() {
        assertThatThrownBy(() -> FinishedAnimesCursorDto.of(SortOption.RATING_DESC, 18L, "4.0"))
                .isInstanceOf(CustomException.class);
        assertThatThrownBy(() -> FinishedAnimesCursorDto.of(SortOption.RATING_DESC, 18L, "abc,2025-11-01 09:12:45"))
                .isInstanceOf(CustomException.class);
        assertThatThrownBy(() -> FinishedAnimesCursorDto.of(SortOption.RATING_DESC, 18L, null))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("응답 lastValue를 정렬별 형식으로 만든다")
    void toLastValue() throws Exception {
        FinishedAnimesAllTitleDto rated = row(4.0, LocalDateTime.of(2025, 11, 8, 14, 23, 11), LocalDate.of(2025, 7, 6));
        FinishedAnimesAllTitleDto unrated = row(null, LocalDateTime.of(2025, 11, 8, 14, 23, 11), null);

        assertThat(FinishedAnimesCursorDto.toLastValue(SortOption.LATEST, rated)).isEqualTo("2025-11-08 14:23:11");
        assertThat(FinishedAnimesCursorDto.toLastValue(SortOption.START_DATE, rated)).isEqualTo("2025-07-06");
        assertThat(FinishedAnimesCursorDto.toLastValue(SortOption.START_DATE, unrated)).isNull();
        assertThat(FinishedAnimesCursorDto.toLastValue(SortOption.RATING_DESC, rated)).isEqualTo("4.0,2025-11-08 14:23:11");
        assertThat(FinishedAnimesCursorDto.toLastValue(SortOption.RATING_ASC, unrated)).isEqualTo(",2025-11-08 14:23:11");
    }

    @Test
    @DisplayName("응답으로 만든 lastValue를 다시 넣으면 같은 값으로 해석된다")
    void roundTrip() throws Exception {
        FinishedAnimesAllTitleDto last = row(3.5, LocalDateTime.of(2025, 1, 2, 3, 4, 5), null);
        String lastValue = FinishedAnimesCursorDto.toLastValue(SortOption.RATING_DESC, last);

        FinishedAnimesCursorDto sut = FinishedAnimesCursorDto.of(SortOption.RATING_DESC, 1L, lastValue);

        assertThat(sut.getLastRating()).isEqualTo(3.5);
        assertThat(sut.getLastSortedAt()).isEqualTo(last.getSortedAt());
    }

    // MyBatis가 리플렉션으로 채우는 DTO라 생성자/세터가 없어 같은 방식으로 값을 넣는다
    private FinishedAnimesAllTitleDto row(Double myRating, LocalDateTime sortedAt, LocalDate startDate) throws Exception {
        FinishedAnimesAllTitleDto dto = new FinishedAnimesAllTitleDto();
        set(dto, "myRating", myRating);
        set(dto, "sortedAt", sortedAt);
        set(dto, "startDate", startDate);
        return dto;
    }

    private void set(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
