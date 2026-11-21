package com.anipick.backend.anime.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 애니 상세 소셜 지표 (방영 상태에 따라 노출할 시청 상태 수)
 * 미노출이면 phase, metric, count 모두 null
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AnimeSocialIndicator {
    // 선택된 지표의 수가 이 값 미만이면 모든 단계에서 미노출
    private static final long MIN_EXPOSURE_COUNT = 5L;
    // 방영 시작일 포함 7일 동안은 '보는 중'만 노출
    private static final long SEASON_START_DAYS = 7L;

    private final AnimeSocialPhase phase;
    private final AnimeSocialMetric metric;
    private final Long count;

    /**
     * @param status         애니 방영 상태
     * @param startDate      방영 시작일 (null 가능)
     * @param today          기준 날짜 (Asia/Seoul)
     * @param watchlistCount WATCHLIST 수
     * @param watchingCount  WATCHING 수
     * @param finishedCount  FINISHED 수
     */
    public static AnimeSocialIndicator of(
            AnimeStatus status,
            LocalDate startDate,
            LocalDate today,
            long watchlistCount,
            long watchingCount,
            long finishedCount
    ) {
        if (status == null) {
            return hidden();
        }
        return switch (status) {
            case NOT_YET_RELEASED ->
                    exposeIfEnough(AnimeSocialPhase.NOT_YET_RELEASED, AnimeSocialMetric.WANT_TO_WATCH, watchlistCount);
            case RELEASING -> {
                // 시작일을 모르면 방영 초기인지 판단할 수 없으므로 미노출
                if (startDate == null) {
                    yield hidden();
                }
                if (today.isBefore(startDate.plusDays(SEASON_START_DAYS))) {
                    yield exposeIfEnough(AnimeSocialPhase.SEASON_START, AnimeSocialMetric.WATCHING, watchingCount);
                }
                yield compareWatchingAndFinished(AnimeSocialPhase.RELEASING, watchingCount, finishedCount);
            }
            case FINISHED -> compareWatchingAndFinished(AnimeSocialPhase.FINISHED, watchingCount, finishedCount);
            case CANCELLED, HIATUS -> hidden();
        };
    }

    public static AnimeSocialIndicator hidden() {
        return new AnimeSocialIndicator(null, null, null);
    }

    // 보는 중 vs 다 본 애니 중 큰 쪽, 동수면 다 본 애니
    private static AnimeSocialIndicator compareWatchingAndFinished(
            AnimeSocialPhase phase,
            long watchingCount,
            long finishedCount
    ) {
        if (watchingCount > finishedCount) {
            return exposeIfEnough(phase, AnimeSocialMetric.WATCHING, watchingCount);
        }
        return exposeIfEnough(phase, AnimeSocialMetric.COMPLETED, finishedCount);
    }

    private static AnimeSocialIndicator exposeIfEnough(
            AnimeSocialPhase phase,
            AnimeSocialMetric metric,
            long count
    ) {
        if (count < MIN_EXPOSURE_COUNT) {
            return hidden();
        }
        return new AnimeSocialIndicator(phase, metric, count);
    }
}
