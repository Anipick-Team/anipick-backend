package com.anipick.backend.community.service;

import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.common.util.LocalizationUtil;
import com.anipick.backend.community.domain.CommunityPostSort;
import com.anipick.backend.community.dto.FeedPageDto;
import com.anipick.backend.community.dto.FeedPostItemDto;
import com.anipick.backend.community.dto.FeedPostRawDto;
import com.anipick.backend.community.mapper.CommunityFeedMapper;
import com.anipick.backend.user.domain.UserAnimeOfStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 커뮤니티 메인 게시글 피드(전체 / 시청중 / 실시간 인기).
 */
@Service
@RequiredArgsConstructor
public class CommunityFeedService {

    private final CommunityFeedMapper communityFeedMapper;

    private static final DateTimeFormatter PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy. MM. dd");

    /**
     * 실시간 인기 집계 기간. 게시판 인기순의 주간(popularWeekly)과 같은 기준(현재 시각 - 1주).
     */
    private static final CommunityPostSort POPULAR_PERIOD = CommunityPostSort.POPULAR_WEEKLY;

    @Transactional(readOnly = true)
    public FeedPageDto getAllFeed(Long lastId, int size, Long userId) {
        validateSize(size);
        List<FeedPostRawDto> raws = communityFeedMapper.selectAllFeedPosts(userId, lastId, size);
        return toLatestPage(raws);
    }

    @Transactional(readOnly = true)
    public FeedPageDto getWatchingFeed(Long lastId, int size, Long userId) {
        validateSize(size);
        List<FeedPostRawDto> raws = communityFeedMapper.selectWatchingFeedPosts(
                userId, UserAnimeOfStatus.WATCHING.toString(), lastId, size);
        return toLatestPage(raws);
    }

    @Transactional(readOnly = true)
    public FeedPageDto getPopularFeed(String lastValue, Long lastId, int size, Long userId) {
        validateSize(size);
        Long lastValueLong = parsePopularCursorValue(lastValue, lastId);
        LocalDateTime periodStart = POPULAR_PERIOD.periodStart(LocalDateTime.now());

        List<FeedPostRawDto> raws = communityFeedMapper.selectPopularFeedPosts(
                userId, periodStart, lastValueLong, lastId, size);

        CursorDto cursor;
        if (raws.isEmpty()) {
            cursor = CursorDto.of(null, null, null);
        } else {
            FeedPostRawDto last = raws.get(raws.size() - 1);
            cursor = CursorDto.of(null, last.getPostId(), String.valueOf(last.getPeriodLikeCount()));
        }
        return FeedPageDto.builder()
                .cursor(cursor)
                .posts(toItems(raws))
                .build();
    }

    private FeedPageDto toLatestPage(List<FeedPostRawDto> raws) {
        Long lastId = raws.isEmpty() ? null : raws.get(raws.size() - 1).getPostId();
        return FeedPageDto.builder()
                .cursor(CursorDto.of(lastId))
                .posts(toItems(raws))
                .build();
    }

    /**
     * 페이지 크기는 1 이상이어야 한다. 음수는 MySQL LIMIT 에서 SQL 오류가 나고, 0 은 빈 페이지만 반복된다.
     */
    static void validateSize(int size) {
        if (size < 1) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    /**
     * 인기 커서(lastValue = 기간 내 좋아요 수, lastId = post_id) 파싱.
     * 둘은 항상 쌍으로 와야 한다. 한쪽만 오면 쿼리의 커서 조건이 무시되어 같은 페이지가 반복되므로 요청 단계에서 막는다.
     * (CommunityBoardService 의 게시판 인기순 커서와 같은 규칙)
     */
    static Long parsePopularCursorValue(String lastValue, Long lastId) {
        boolean hasLastValue = lastValue != null && !lastValue.isBlank();

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

    private List<FeedPostItemDto> toItems(List<FeedPostRawDto> raws) {
        return raws.stream()
                .map(this::toItem)
                .toList();
    }

    private FeedPostItemDto toItem(FeedPostRawDto raw) {
        String seriesTitle = LocalizationUtil.pickTitle(
                raw.getSeriesTitleMan(), raw.getSeriesTitleKor(), raw.getSeriesTitleEng(),
                raw.getSeriesTitleRom(), raw.getSeriesTitleNat());

        return FeedPostItemDto.builder()
                .postId(raw.getPostId())
                .seriesId(raw.getSeriesId())
                .seriesTitle(seriesTitle)
                .userId(raw.getUserId())
                .nickname(raw.getNickname())
                .profileImageId(raw.getProfileImageId())
                .title(raw.getTitle())
                .content(raw.getContent())
                .thumbnailImageId(raw.getThumbnailImageId())
                .imageCount(raw.getImageCount())
                .isSpoiler(raw.getIsSpoiler())
                .viewCount(raw.getViewCount())
                .likeCount(raw.getLikeCount())
                .commentCount(raw.getCommentCount())
                .createdAt(LocalDateTime.parse(raw.getCreatedAt(), PARSER).format(FORMATTER))
                .build();
    }
}
