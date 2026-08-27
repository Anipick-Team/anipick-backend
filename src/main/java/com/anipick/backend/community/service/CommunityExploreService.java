package com.anipick.backend.community.service;

import com.anipick.backend.anime.dto.GenreDto;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.common.util.LocalizationUtil;
import com.anipick.backend.community.domain.CommunityBoardSort;
import com.anipick.backend.community.dto.ExploreBoardItemDto;
import com.anipick.backend.community.dto.ExploreBoardPageDto;
import com.anipick.backend.community.dto.ExploreBoardRawDto;
import com.anipick.backend.community.dto.SeriesGenreRawDto;
import com.anipick.backend.community.mapper.CommunityExploreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityExploreService {

    private final CommunityExploreMapper communityExploreMapper;

    private static final DateTimeFormatter PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(readOnly = true)
    public ExploreBoardPageDto getBoards(String sortCode, String keyword, String lastValue, Long lastId, int size) {
        CommunityBoardSort sort = CommunityBoardSort.of(sortCode);
        // 탐색은 빈 키워드 허용 (EMPTY_KEYWORD 미적용). blank 는 필터 미적용으로 정규화
        String normalizedKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        Long total = communityExploreMapper.countExploreBoards(normalizedKeyword);

        List<ExploreBoardRawDto> rawItems;
        if (sort == CommunityBoardSort.POPULAR) {
            Long lastValueLong = parsePopularCursorValue(lastValue, lastId);
            LocalDateTime periodStart = LocalDateTime.now().minusWeeks(1);
            rawItems = communityExploreMapper.selectPopularBoards(normalizedKeyword, periodStart, lastValueLong, lastId, size);
        } else {
            String lastValueDateTime = parseLatestCursorValue(lastValue, lastId);
            rawItems = communityExploreMapper.selectLatestBoards(normalizedKeyword, lastValueDateTime, lastId, size);
        }

        Map<Long, List<GenreDto>> genresBySeries = findGenresBySeries(rawItems);

        List<ExploreBoardItemDto> boards = rawItems.stream()
                .map(raw -> toItemDto(raw, genresBySeries.getOrDefault(raw.getSeriesId(), Collections.emptyList())))
                .toList();

        CursorDto cursor = buildCursor(sort, rawItems);

        return ExploreBoardPageDto.builder()
                .count(total)
                .cursor(cursor)
                .boards(boards)
                .build();
    }

    /**
     * 인기순 커서(lastValue = 최근 1주일 게시글 수, lastId = series_id) 파싱. 둘은 항상 쌍으로 와야 한다.
     */
    private Long parsePopularCursorValue(String lastValue, Long lastId) {
        boolean hasLastValue = lastValue != null && !lastValue.isBlank();

        // 첫 페이지
        if (!hasLastValue && lastId == null) {
            return null;
        }
        if (!hasLastValue || lastId == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        try {
            return Long.parseLong(lastValue.trim());
        } catch (NumberFormatException e) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    /**
     * 최신순 커서 파싱. lastValue = 마지막 시리즈의 최근 게시글 시각(yyyy-MM-dd HH:mm:ss).
     * 게시글 없는 시리즈 구간(NULL)에 들어서면 lastValue 없이 lastId 만으로 이어진다.
     * - lastValue + lastId : 값 구간 커서
     * - lastId 만          : NULL 구간 커서
     * - lastValue 만       : 잘못된 요청
     */
    private String parseLatestCursorValue(String lastValue, Long lastId) {
        boolean hasLastValue = lastValue != null && !lastValue.isBlank();

        if (!hasLastValue) {
            return null;
        }
        if (lastId == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        String trimmed = lastValue.trim();
        try {
            LocalDateTime.parse(trimmed, PARSER);
        } catch (DateTimeParseException e) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        return trimmed;
    }

    private Map<Long, List<GenreDto>> findGenresBySeries(List<ExploreBoardRawDto> rawItems) {
        if (rawItems.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> seriesIds = rawItems.stream()
                .map(ExploreBoardRawDto::getSeriesId)
                .toList();
        return communityExploreMapper.selectRepresentativeGenresBySeriesIds(seriesIds).stream()
                .collect(Collectors.groupingBy(
                        SeriesGenreRawDto::getSeriesId,
                        Collectors.mapping(raw -> new GenreDto(raw.getId(), raw.getName()), Collectors.toList())
                ));
    }

    private ExploreBoardItemDto toItemDto(ExploreBoardRawDto raw, List<GenreDto> genres) {
        String title = LocalizationUtil.pickTitle(
                null, raw.getTitleKor(), raw.getTitleEng(), raw.getTitleRom(), raw.getTitleNat());
        return ExploreBoardItemDto.builder()
                .seriesId(raw.getSeriesId())
                .title(title)
                .coverImageUrl(raw.getCoverImageUrl())
                .genres(genres)
                .build();
    }

    private CursorDto buildCursor(CommunityBoardSort sort, List<ExploreBoardRawDto> rawItems) {
        if (rawItems.isEmpty()) {
            return CursorDto.of(sort.getCode(), null, null);
        }
        ExploreBoardRawDto last = rawItems.get(rawItems.size() - 1);
        if (sort == CommunityBoardSort.POPULAR) {
            return CursorDto.of(sort.getCode(), last.getSeriesId(), String.valueOf(last.getPeriodPostCount()));
        }
        // latest: NULL 구간이면 lastValue 없이 lastId 로만 이어감 (lastPostedAt null → NON_NULL 로 응답에서 제외)
        return CursorDto.of(sort.getCode(), last.getSeriesId(), last.getLastPostedAt());
    }
}
