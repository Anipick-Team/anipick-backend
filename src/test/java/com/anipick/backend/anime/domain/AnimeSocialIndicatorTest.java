package com.anipick.backend.anime.domain;

import com.anipick.backend.user.domain.UserAnimeOfStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AnimeSocialIndicatorTest {
    // 2026-11-02 은 월요일
    private static final LocalDate START_DATE = LocalDate.of(2026, 11, 2);
    private static final LocalDate AFTER_SEASON_START = START_DATE.plusDays(30);

    private static void assertHidden(AnimeSocialIndicator indicator) {
        assertNull(indicator.getPhase());
        assertNull(indicator.getMetric());
        assertNull(indicator.getCount());
    }

    private static void assertExposed(
            AnimeSocialIndicator indicator,
            AnimeSocialPhase phase,
            AnimeSocialMetric metric,
            long count
    ) {
        assertEquals(phase, indicator.getPhase());
        assertEquals(metric, indicator.getMetric());
        assertEquals(count, indicator.getCount());
    }

    // ===== hidden() =====

    @Test
    @DisplayName("hidden() 은 phase, metric, count 가 모두 null 이다.")
    void hiddenAllNull() {
        assertHidden(AnimeSocialIndicator.hidden());
    }

    // ===== NOT_YET_RELEASED =====

    @Test
    @DisplayName("방영 예정이면 (NOT_YET_RELEASED, WANT_TO_WATCH, 볼 애니 수) 로 노출한다.")
    void notYetReleasedExposesWatchlist() {
        // when
        AnimeSocialIndicator indicator = AnimeSocialIndicator.of(
                AnimeStatus.NOT_YET_RELEASED, START_DATE, START_DATE.minusDays(10), 12L, 100L, 200L);
        // then
        assertExposed(indicator, AnimeSocialPhase.NOT_YET_RELEASED, AnimeSocialMetric.WANT_TO_WATCH, 12L);
    }

    @Test
    @DisplayName("방영 예정이면 startDate 가 null 이어도 볼 애니 수로 노출한다.")
    void notYetReleasedWithNullStartDate() {
        AnimeSocialIndicator indicator = AnimeSocialIndicator.of(
                AnimeStatus.NOT_YET_RELEASED, null, START_DATE, 5L, 0L, 0L);
        assertExposed(indicator, AnimeSocialPhase.NOT_YET_RELEASED, AnimeSocialMetric.WANT_TO_WATCH, 5L);
    }

    @Test
    @DisplayName("방영 예정에서 볼 애니 4 는 미노출, 5 는 노출한다. (보는 중/다 본 애니가 커도 무관)")
    void notYetReleasedThreshold() {
        assertHidden(AnimeSocialIndicator.of(
                AnimeStatus.NOT_YET_RELEASED, START_DATE, START_DATE.minusDays(1), 4L, 100L, 100L));
        assertExposed(AnimeSocialIndicator.of(
                        AnimeStatus.NOT_YET_RELEASED, START_DATE, START_DATE.minusDays(1), 5L, 0L, 0L),
                AnimeSocialPhase.NOT_YET_RELEASED, AnimeSocialMetric.WANT_TO_WATCH, 5L);
    }

    // ===== RELEASING - SEASON_START =====

    @Test
    @DisplayName("방영 중이고 startDate 가 null 이면 미노출한다.")
    void releasingWithNullStartDateIsHidden() {
        assertHidden(AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, null, START_DATE, 100L, 100L, 100L));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 3, 6})
    @DisplayName("방영 중이고 시작일 ~ 시작일+6일이면 SEASON_START 로 보는 중 수를 노출한다.")
    void seasonStartWithinSevenDays(long daysAfterStart) {
        AnimeSocialIndicator indicator = AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, START_DATE.plusDays(daysAfterStart), 0L, 10L, 3L);
        assertExposed(indicator, AnimeSocialPhase.SEASON_START, AnimeSocialMetric.WATCHING, 10L);
    }

    @Test
    @DisplayName("방영 중이고 시작일+7일이면 SEASON_START 가 아닌 RELEASING 이다.")
    void releasingFromSeventhDay() {
        AnimeSocialIndicator indicator = AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, START_DATE.plusDays(7), 0L, 10L, 3L);
        assertExposed(indicator, AnimeSocialPhase.RELEASING, AnimeSocialMetric.WATCHING, 10L);
    }

    @Test
    @DisplayName("다 본 애니가 더 많을 때 시작일+6일은 SEASON_START/WATCHING, 시작일+7일은 RELEASING/COMPLETED 로 바뀐다.")
    void seasonStartBoundarySwitchesToComparison() {
        // given
        long watching = 6L;
        long finished = 9L;
        // when
        AnimeSocialIndicator dayPlus6 = AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, START_DATE.plusDays(6), 0L, watching, finished);
        AnimeSocialIndicator dayPlus7 = AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, START_DATE.plusDays(7), 0L, watching, finished);
        // then
        assertExposed(dayPlus6, AnimeSocialPhase.SEASON_START, AnimeSocialMetric.WATCHING, 6L);
        assertExposed(dayPlus7, AnimeSocialPhase.RELEASING, AnimeSocialMetric.COMPLETED, 9L);
    }

    @Test
    @DisplayName("시작일+6일에 보는 중 4 면 미노출, 시작일+7일에는 다 본 애니 5 로 노출된다.")
    void seasonStartBoundaryWithThreshold() {
        assertHidden(AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, START_DATE.plusDays(6), 0L, 4L, 5L));
        assertExposed(AnimeSocialIndicator.of(
                        AnimeStatus.RELEASING, START_DATE, START_DATE.plusDays(7), 0L, 4L, 5L),
                AnimeSocialPhase.RELEASING, AnimeSocialMetric.COMPLETED, 5L);
    }

    @Test
    @DisplayName("연도를 넘는 경우에도 12월 28일 시작이면 1월 3일까지 SEASON_START, 1월 4일부터 RELEASING 이다.")
    void seasonStartBoundaryAcrossYear() {
        // given
        LocalDate start = LocalDate.of(2026, 12, 28);
        // when & then
        assertEquals(AnimeSocialPhase.SEASON_START, AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, start, LocalDate.of(2027, 1, 3), 0L, 5L, 0L).getPhase());
        assertEquals(AnimeSocialPhase.RELEASING, AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, start, LocalDate.of(2027, 1, 4), 0L, 5L, 0L).getPhase());
    }

    @Test
    @DisplayName("월요일 시작이면 같은 주 일요일까지 SEASON_START, 다음 월요일부터 RELEASING 이다.")
    void mondayToSundayExample() {
        // given
        assertEquals(DayOfWeek.MONDAY, START_DATE.getDayOfWeek());
        LocalDate sunday = START_DATE.plusDays(6);
        LocalDate nextMonday = START_DATE.plusDays(7);
        assertEquals(DayOfWeek.SUNDAY, sunday.getDayOfWeek());
        assertEquals(DayOfWeek.MONDAY, nextMonday.getDayOfWeek());

        // when & then
        for (LocalDate day = START_DATE; !day.isAfter(sunday); day = day.plusDays(1)) {
            assertEquals(AnimeSocialPhase.SEASON_START,
                    AnimeSocialIndicator.of(AnimeStatus.RELEASING, START_DATE, day, 0L, 8L, 0L).getPhase(),
                    "day=" + day);
        }
        assertEquals(AnimeSocialPhase.RELEASING,
                AnimeSocialIndicator.of(AnimeStatus.RELEASING, START_DATE, nextMonday, 0L, 8L, 0L).getPhase());
    }

    @Test
    @DisplayName("SEASON_START 에서는 다 본 애니가 더 많아도 비교하지 않고 보는 중 수를 노출한다.")
    void seasonStartDoesNotCompare() {
        AnimeSocialIndicator indicator = AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, START_DATE.plusDays(2), 0L, 6L, 50L);
        assertExposed(indicator, AnimeSocialPhase.SEASON_START, AnimeSocialMetric.WATCHING, 6L);
    }

    @Test
    @DisplayName("SEASON_START 에서 보는 중 4 는 다 본 애니가 많아도 미노출, 5 는 노출한다.")
    void seasonStartThreshold() {
        LocalDate today = START_DATE.plusDays(1);
        assertHidden(AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, today, 100L, 4L, 100L));
        assertExposed(AnimeSocialIndicator.of(
                        AnimeStatus.RELEASING, START_DATE, today, 0L, 5L, 0L),
                AnimeSocialPhase.SEASON_START, AnimeSocialMetric.WATCHING, 5L);
    }

    @Test
    @DisplayName("방영 중인데 startDate 가 미래여도 SEASON_START 로 판정한다.")
    void futureStartDateIsSeasonStart() {
        AnimeSocialIndicator indicator = AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, START_DATE.minusDays(30), 0L, 7L, 100L);
        assertExposed(indicator, AnimeSocialPhase.SEASON_START, AnimeSocialMetric.WATCHING, 7L);
    }

    // ===== RELEASING - 비교 =====

    @Test
    @DisplayName("RELEASING 에서 보는 중 > 다 본 애니면 WATCHING 으로 보는 중 수를 노출한다.")
    void releasingWatchingWins() {
        AnimeSocialIndicator indicator = AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, AFTER_SEASON_START, 999L, 20L, 19L);
        assertExposed(indicator, AnimeSocialPhase.RELEASING, AnimeSocialMetric.WATCHING, 20L);
    }

    @Test
    @DisplayName("RELEASING 에서 다 본 애니 > 보는 중이면 COMPLETED 로 다 본 애니 수를 노출한다.")
    void releasingCompletedWins() {
        AnimeSocialIndicator indicator = AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, AFTER_SEASON_START, 0L, 19L, 20L);
        assertExposed(indicator, AnimeSocialPhase.RELEASING, AnimeSocialMetric.COMPLETED, 20L);
    }

    @Test
    @DisplayName("RELEASING 에서 보는 중과 다 본 애니가 동수면 COMPLETED 를 노출한다.")
    void releasingTieIsCompleted() {
        AnimeSocialIndicator indicator = AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, AFTER_SEASON_START, 0L, 10L, 10L);
        assertExposed(indicator, AnimeSocialPhase.RELEASING, AnimeSocialMetric.COMPLETED, 10L);
    }

    @Test
    @DisplayName("RELEASING 에서 선택된 수 4 는 미노출, 5 는 노출한다.")
    void releasingThreshold() {
        assertHidden(AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, AFTER_SEASON_START, 0L, 4L, 3L));
        assertExposed(AnimeSocialIndicator.of(
                        AnimeStatus.RELEASING, START_DATE, AFTER_SEASON_START, 0L, 5L, 3L),
                AnimeSocialPhase.RELEASING, AnimeSocialMetric.WATCHING, 5L);
        assertHidden(AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, AFTER_SEASON_START, 0L, 3L, 4L));
        assertExposed(AnimeSocialIndicator.of(
                        AnimeStatus.RELEASING, START_DATE, AFTER_SEASON_START, 0L, 3L, 5L),
                AnimeSocialPhase.RELEASING, AnimeSocialMetric.COMPLETED, 5L);
    }

    @Test
    @DisplayName("RELEASING 에서 동수 4 는 미노출, 동수 5 는 COMPLETED 로 노출한다.")
    void releasingTieThreshold() {
        assertHidden(AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, AFTER_SEASON_START, 0L, 4L, 4L));
        assertExposed(AnimeSocialIndicator.of(
                        AnimeStatus.RELEASING, START_DATE, AFTER_SEASON_START, 0L, 5L, 5L),
                AnimeSocialPhase.RELEASING, AnimeSocialMetric.COMPLETED, 5L);
    }

    @Test
    @DisplayName("RELEASING 에서 선택된 쪽이 5 미만이면 볼 애니가 많아도 미노출한다. (watching 4 > finished 3)")
    void releasingSelectedSideDecides() {
        assertHidden(AnimeSocialIndicator.of(
                AnimeStatus.RELEASING, START_DATE, AFTER_SEASON_START, 100L, 4L, 3L));
    }

    // ===== FINISHED =====

    @Test
    @DisplayName("FINISHED 에서 보는 중 > 다 본 애니면 WATCHING 으로 노출한다.")
    void finishedWatchingWins() {
        AnimeSocialIndicator indicator = AnimeSocialIndicator.of(
                AnimeStatus.FINISHED, START_DATE, AFTER_SEASON_START, 0L, 30L, 29L);
        assertExposed(indicator, AnimeSocialPhase.FINISHED, AnimeSocialMetric.WATCHING, 30L);
    }

    @Test
    @DisplayName("FINISHED 에서 다 본 애니 > 보는 중이면 COMPLETED 로 노출한다.")
    void finishedCompletedWins() {
        AnimeSocialIndicator indicator = AnimeSocialIndicator.of(
                AnimeStatus.FINISHED, START_DATE, AFTER_SEASON_START, 0L, 5L, 40L);
        assertExposed(indicator, AnimeSocialPhase.FINISHED, AnimeSocialMetric.COMPLETED, 40L);
    }

    @Test
    @DisplayName("FINISHED 에서 동수면 COMPLETED 를 노출한다.")
    void finishedTieIsCompleted() {
        AnimeSocialIndicator indicator = AnimeSocialIndicator.of(
                AnimeStatus.FINISHED, START_DATE, AFTER_SEASON_START, 0L, 7L, 7L);
        assertExposed(indicator, AnimeSocialPhase.FINISHED, AnimeSocialMetric.COMPLETED, 7L);
    }

    @Test
    @DisplayName("FINISHED 는 startDate 가 null 이어도 정상 판정한다.")
    void finishedWithNullStartDate() {
        AnimeSocialIndicator indicator = AnimeSocialIndicator.of(
                AnimeStatus.FINISHED, null, AFTER_SEASON_START, 0L, 3L, 9L);
        assertExposed(indicator, AnimeSocialPhase.FINISHED, AnimeSocialMetric.COMPLETED, 9L);
    }

    @Test
    @DisplayName("FINISHED 는 시작일+7일 이내여도 SEASON_START 가 아니라 FINISHED 비교 로직을 따른다.")
    void finishedWithinSevenDaysIsNotSeasonStart() {
        AnimeSocialIndicator indicator = AnimeSocialIndicator.of(
                AnimeStatus.FINISHED, START_DATE, START_DATE.plusDays(1), 0L, 6L, 20L);
        assertExposed(indicator, AnimeSocialPhase.FINISHED, AnimeSocialMetric.COMPLETED, 20L);
    }

    @Test
    @DisplayName("FINISHED 에서 선택된 수 4 는 미노출, 5 는 노출한다.")
    void finishedThreshold() {
        assertHidden(AnimeSocialIndicator.of(
                AnimeStatus.FINISHED, START_DATE, AFTER_SEASON_START, 0L, 4L, 3L));
        assertExposed(AnimeSocialIndicator.of(
                        AnimeStatus.FINISHED, START_DATE, AFTER_SEASON_START, 0L, 5L, 3L),
                AnimeSocialPhase.FINISHED, AnimeSocialMetric.WATCHING, 5L);
        assertHidden(AnimeSocialIndicator.of(
                AnimeStatus.FINISHED, START_DATE, AFTER_SEASON_START, 0L, 3L, 4L));
        assertExposed(AnimeSocialIndicator.of(
                        AnimeStatus.FINISHED, START_DATE, AFTER_SEASON_START, 0L, 3L, 5L),
                AnimeSocialPhase.FINISHED, AnimeSocialMetric.COMPLETED, 5L);
    }

    @Test
    @DisplayName("FINISHED 에서 watching 4 > finished 3 이면 볼 애니가 많아도 미노출한다.")
    void finishedSelectedSideDecides() {
        assertHidden(AnimeSocialIndicator.of(
                AnimeStatus.FINISHED, START_DATE, AFTER_SEASON_START, 100L, 4L, 3L));
    }

    // ===== 0 건 =====

    @ParameterizedTest
    @EnumSource(AnimeStatus.class)
    @DisplayName("모든 수가 0 이면 어떤 상태든 미노출한다.")
    void allZeroIsHidden(AnimeStatus status) {
        assertHidden(AnimeSocialIndicator.of(status, START_DATE, AFTER_SEASON_START, 0L, 0L, 0L));
        assertHidden(AnimeSocialIndicator.of(status, START_DATE, START_DATE, 0L, 0L, 0L));
    }

    // ===== 미노출 상태 =====

    @ParameterizedTest
    @EnumSource(value = AnimeStatus.class, names = {"CANCELLED", "HIATUS"})
    @DisplayName("CANCELLED, HIATUS 는 수가 충분해도 미노출한다.")
    void cancelledAndHiatusAreHidden(AnimeStatus status) {
        assertHidden(AnimeSocialIndicator.of(status, START_DATE, AFTER_SEASON_START, 100L, 100L, 100L));
        assertHidden(AnimeSocialIndicator.of(status, START_DATE, START_DATE, 100L, 100L, 100L));
        assertHidden(AnimeSocialIndicator.of(status, null, AFTER_SEASON_START, 100L, 100L, 100L));
    }

    // ===== AnimeSocialMetric 매핑 =====

    @Test
    @DisplayName("소셜 지표 metric 은 볼 애니=WATCHLIST, 보는 중=WATCHING, 다 본 애니=FINISHED 시청 상태와 매핑된다.")
    void metricMapsToWatchStatus() {
        assertEquals(UserAnimeOfStatus.WATCHLIST, AnimeSocialMetric.WANT_TO_WATCH.getWatchStatus());
        assertEquals(UserAnimeOfStatus.WATCHING, AnimeSocialMetric.WATCHING.getWatchStatus());
        assertEquals(UserAnimeOfStatus.FINISHED, AnimeSocialMetric.COMPLETED.getWatchStatus());
    }

    @Test
    @DisplayName("status 가 null 이면 수가 충분해도 미노출한다.")
    void nullStatusIsHidden() {
        assertHidden(AnimeSocialIndicator.of(null, START_DATE, AFTER_SEASON_START, 100L, 100L, 100L));
        assertHidden(AnimeSocialIndicator.of(null, null, AFTER_SEASON_START, 100L, 100L, 100L));
    }
}
