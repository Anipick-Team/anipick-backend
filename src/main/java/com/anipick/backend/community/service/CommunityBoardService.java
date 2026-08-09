package com.anipick.backend.community.service;

import com.anipick.backend.anime.dto.GenreDto;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.common.util.LocalizationUtil;
import com.anipick.backend.community.domain.CommunityPostSort;
import com.anipick.backend.community.dto.BoardByAnimeResultDto;
import com.anipick.backend.community.dto.BoardInfoRawDto;
import com.anipick.backend.community.dto.PostListItemDto;
import com.anipick.backend.community.dto.PostListPageDto;
import com.anipick.backend.community.mapper.CommunityBoardMapper;
import com.anipick.backend.community.mapper.CommunityPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityBoardService {

    private final CommunityBoardMapper communityBoardMapper;
    private final CommunityPostMapper communityPostMapper;

    private static final DateTimeFormatter PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy. MM. dd");

    @Transactional(readOnly = true)
    public BoardByAnimeResultDto getBoardByAnime(Long animeId) {
        BoardInfoRawDto raw = communityBoardMapper.selectBoardByAnimeId(animeId);
        if (raw == null) {
            // 애니 자체가 존재하지 않음
            throw new CustomException(ErrorCode.ANIME_NOT_FOUND);
        }

        // 시리즈 매핑이 없으면 게시판 없음 → 프론트 팝업용. 애니 자체 정보로 fallback.
        if (raw.getSeriesId() == null) {
            String animeTitle = LocalizationUtil.pickTitle(
                    raw.getAnimeTitleMan(), raw.getAnimeTitleKor(), raw.getAnimeTitleEng(),
                    raw.getAnimeTitleRom(), raw.getAnimeTitleNat());
            List<GenreDto> genres = communityBoardMapper.selectGenresByAnimeId(animeId);

            // postCount 는 세팅하지 않아 null → 응답에서 제외(@JsonInclude NON_NULL)
            return BoardByAnimeResultDto.builder()
                    .hasBoard(false)
                    .seriesId(null)
                    .title(animeTitle)
                    .coverImageUrl(raw.getAnimeCoverImageUrl())
                    .genres(genres)
                    .build();
        }

        String title = LocalizationUtil.pickTitle(
                null, raw.getTitleKor(), raw.getTitleEng(), raw.getTitleRom(), raw.getTitleNat());
        List<GenreDto> genres = communityBoardMapper.selectRepresentativeGenresBySeriesId(raw.getSeriesId());
        Long postCount = communityBoardMapper.countPostsBySeriesId(raw.getSeriesId());

        return BoardByAnimeResultDto.builder()
                .hasBoard(true)
                .seriesId(raw.getSeriesId())
                .title(title)
                .coverImageUrl(raw.getCoverImageUrl())
                .genres(genres)
                .postCount(postCount)
                .build();
    }

    @Transactional(readOnly = true)
    public PostListPageDto getPosts(Long seriesId, String sortCode, String lastValue, Long lastId, int size, Long userId) {
        if (!communityBoardMapper.existsSeries(seriesId)) {
            throw new CustomException(ErrorCode.SERIES_NOT_FOUND);
        }

        CommunityPostSort sort = CommunityPostSort.of(sortCode);
        Long total = communityPostMapper.countBoardPosts(seriesId, userId);

        List<PostListItemDto> rawItems;
        if (sort.isPopular()) {
            LocalDateTime periodStart = sort.periodStart(LocalDateTime.now());
            Long lastValueLong = parsePopularCursorValue(lastValue, lastId);
            rawItems = communityPostMapper.selectPopularPosts(seriesId, userId, periodStart, lastValueLong, lastId, size);
        } else {
            rawItems = communityPostMapper.selectLatestPosts(seriesId, userId, lastId, size);
        }

        List<PostListItemDto> items = rawItems.stream()
                .map(this::formatCreatedAt)
                .toList();

        CursorDto cursor = buildCursor(sort, rawItems);

        return PostListPageDto.builder()
                .count(total)
                .cursor(cursor)
                .posts(items)
                .build();
    }

    /**
     * 인기순 커서(lastValue = 기간 내 좋아요 수, lastId = post_id) 파싱.
     * 둘은 항상 쌍으로 와야 한다. 한쪽만 오면 쿼리의 커서 조건이 통째로 무시되어
     * 같은 페이지가 계속 반환되므로(무한 스크롤 루프) 요청 단계에서 막는다.
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

    private CursorDto buildCursor(CommunityPostSort sort, List<PostListItemDto> rawItems) {
        if (rawItems.isEmpty()) {
            return sort.isPopular()
                    ? CursorDto.of(sort.getCode(), null, null)
                    : CursorDto.of(null);
        }
        PostListItemDto last = rawItems.get(rawItems.size() - 1);
        if (sort.isPopular()) {
            return CursorDto.of(sort.getCode(), last.getPostId(), String.valueOf(last.getPeriodLikeCount()));
        }
        return CursorDto.of(last.getPostId());
    }

    private PostListItemDto formatCreatedAt(PostListItemDto raw) {
        return PostListItemDto.builder()
                .postId(raw.getPostId())
                .userId(raw.getUserId())
                .nickname(raw.getNickname())
                .profileImageId(raw.getProfileImageId())
                .title(raw.getTitle())
                .content(raw.getContent())
                .thumbnailImageId(raw.getThumbnailImageId())
                .isSpoiler(raw.getIsSpoiler())
                .viewCount(raw.getViewCount())
                .likeCount(raw.getLikeCount())
                .commentCount(raw.getCommentCount())
                .createdAt(LocalDateTime.parse(raw.getCreatedAt(), PARSER).format(FORMATTER))
                .periodLikeCount(raw.getPeriodLikeCount())
                .build();
    }
}
