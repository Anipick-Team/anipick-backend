package com.anipick.backend.community.service;

import com.anipick.backend.anime.domain.Day;
import com.anipick.backend.anime.dto.AnimeWeekScheduleDto;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.common.util.LocalizationUtil;
import com.anipick.backend.community.dto.LoungePageDto;
import com.anipick.backend.community.dto.LoungeSeriesItemDto;
import com.anipick.backend.community.dto.LoungeSeriesRawDto;
import com.anipick.backend.community.mapper.CommunityLoungeMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 오늘의 방영작 라운지.
 * 홈 요일별 신작(AnimeService.getDayOfTheWeekAnimes)과 같은 Redis 데이터를 쓰고, 애니를 시리즈(게시판) 단위로 묶는다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityLoungeService {

    private final CommunityLoungeMapper communityLoungeMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String DAY_WEEK_CURRENT_KEY = "schedule:week:current";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public LoungePageDto getTodayLounge(Long lastId, int size) {
        Day today = toDay(LocalDate.now(KST).getDayOfWeek());
        return getLounge(today, lastId, size);
    }

    static Day toDay(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> Day.MON;
            case TUESDAY -> Day.TUE;
            case WEDNESDAY -> Day.WED;
            case THURSDAY -> Day.THU;
            case FRIDAY -> Day.FRI;
            case SATURDAY -> Day.SAT;
            case SUNDAY -> Day.SUN;
        };
    }

    /**
     * 페이지 크기는 1 이상이어야 한다(커뮤니티 피드와 같은 규칙). 0 이하면 빈 페이지만 반복된다.
     */
    static void validateSize(int size) {
        if (size < 1) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    /**
     * 1. Redis 요일 데이터를 popularityRank 순으로 정렬
     * 2. 시리즈로 변환(성인·미매핑 애니 제외), 같은 시리즈는 순위가 가장 높은 애니 하나로 합침
     * 3. 정렬된 전체 목록에서 lastId(seriesId) 다음 위치부터 size 개 (홈 요일별 신작과 같은 페이징)
     */
    LoungePageDto getLounge(Day day, Long lastId, int size) {
        validateSize(size);
        List<AnimeWeekScheduleDto> schedules = readSchedules(day);
        if (schedules.isEmpty()) {
            return LoungePageDto.empty();
        }

        List<Long> animeIds = schedules.stream()
                .map(AnimeWeekScheduleDto::getAnimeId)
                .toList();
        Map<Long, LoungeSeriesRawDto> seriesByAnimeId = communityLoungeMapper.selectSeriesByAnimeIds(animeIds).stream()
                .collect(Collectors.toMap(LoungeSeriesRawDto::getAnimeId, Function.identity(), (a, b) -> a));

        List<LoungeSeriesItemDto> sortedSeries = toSortedSeries(schedules, seriesByAnimeId);

        int from = indexAfter(sortedSeries, lastId);
        // from + size 는 size 가 매우 크면 int 오버플로가 나므로 남은 개수와 비교한다 (from <= sortedSeries.size() 보장)
        int to = from + Math.min(size, sortedSeries.size() - from);
        List<LoungeSeriesItemDto> page = from < to ? sortedSeries.subList(from, to) : List.of();

        Long cursorId = page.isEmpty() ? null : page.getLast().getSeriesId();
        return LoungePageDto.builder()
                .count((long) sortedSeries.size())
                .cursor(CursorDto.of(cursorId))
                .series(page)
                .build();
    }

    private List<AnimeWeekScheduleDto> readSchedules(Day day) {
        Object value = redisTemplate.opsForHash().get(DAY_WEEK_CURRENT_KEY, day.name());
        String json = value == null ? null : value.toString();
        if (!StringUtils.hasText(json)) {
            log.warn("[라운지] Redis 데이터 없음 - key={}, day={}", DAY_WEEK_CURRENT_KEY, day.name());
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<AnimeWeekScheduleDto>>() {});
        } catch (Exception e) {
            log.error("[라운지] JSON 파싱 실패 - key={}, day={}, jsonLength={}",
                    DAY_WEEK_CURRENT_KEY, day.name(), json.length(), e);
            return List.of();
        }
    }

    private List<LoungeSeriesItemDto> toSortedSeries(List<AnimeWeekScheduleDto> schedules,
                                                     Map<Long, LoungeSeriesRawDto> seriesByAnimeId) {
        List<AnimeWeekScheduleDto> sorted = schedules.stream()
                .sorted(Comparator.comparing(AnimeWeekScheduleDto::getPopularityRank,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        // 정렬 순서대로 처음 나온 애니가 그 시리즈의 대표(가장 높은 인기 순위)
        Map<Long, LoungeSeriesItemDto> bySeriesId = new LinkedHashMap<>();
        for (AnimeWeekScheduleDto schedule : sorted) {
            LoungeSeriesRawDto raw = seriesByAnimeId.get(schedule.getAnimeId());
            if (raw == null) {
                continue;
            }
            bySeriesId.putIfAbsent(raw.getSeriesId(), LoungeSeriesItemDto.builder()
                    .seriesId(raw.getSeriesId())
                    .title(LocalizationUtil.pickTitle(
                            raw.getTitleMan(), raw.getTitleKor(), raw.getTitleEng(), raw.getTitleRom(), raw.getTitleNat()))
                    .coverImageUrl(raw.getCoverImageUrl())
                    .episode(schedule.getEpisode())
                    .build());
        }
        return new ArrayList<>(bySeriesId.values());
    }

    /**
     * lastId(seriesId) 다음 위치. 없거나 목록에 없으면 0 (AnimeService.getIndex 와 같은 규칙).
     */
    private static int indexAfter(List<LoungeSeriesItemDto> items, Long lastId) {
        if (lastId == null) {
            return 0;
        }
        for (int i = 0; i < items.size(); i++) {
            if (lastId.equals(items.get(i).getSeriesId())) {
                return i + 1;
            }
        }
        return 0;
    }
}
