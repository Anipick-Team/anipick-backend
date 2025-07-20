package com.anipick.backend.mypage.service;

import com.anipick.backend.common.domain.SortOption;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.mypage.domain.MyPageDefaults;
import com.anipick.backend.mypage.dto.*;
import com.anipick.backend.mypage.mapper.MyPageMapper;
import com.anipick.backend.user.domain.User;
import com.anipick.backend.user.domain.UserAnimeOfStatus;
import com.anipick.backend.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageService {
    private final MyPageMapper myPageMapper;
    private final UserMapper userMapper;

    public MyPageResponse getMyPage(Long userId) {
        User user = userMapper.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_BY_EMAIL));

        Long watchListCount = myPageMapper.getMyWatchCount(userId, UserAnimeOfStatus.WATCHLIST.toString());
        Long watchingCount = myPageMapper.getMyWatchCount(userId, UserAnimeOfStatus.WATCHING.toString());
        Long finishedCount = myPageMapper.getMyWatchCount(userId, UserAnimeOfStatus.FINISHED.toString());

        WatchCountDto watchCountDto = WatchCountDto.from(watchListCount, watchingCount, finishedCount);
        List<LikedAnimesDto> likedAnimesDto = myPageMapper.getMyLikedAnimes(userId, null, MyPageDefaults.DEFAULT_PAGE_SIZE);
        List<LikedPersonsDto> likedPersonsDto = myPageMapper.getMyLikedPersons(userId, null, MyPageDefaults.DEFAULT_PAGE_SIZE);

        return MyPageResponse.from(user.getNickname(), user.getProfileImageUrl(), watchCountDto, likedAnimesDto, likedPersonsDto);
    }

    public WatchListAnimesResponse getMyAnimesWatchList(Long userId, String status, Long lastId, Integer size) {
        Long count = myPageMapper.getMyWatchCount(userId, status);
        List<WatchListAnimesDto> watchListAnimes = myPageMapper.getMyWatchListAnimes(userId, status, lastId, size);

        Long newLastId = watchListAnimes.getLast().getUserAnimeStatusId();
        CursorDto cursorDto = CursorDto.of(newLastId);

        return WatchListAnimesResponse.from(count, cursorDto, watchListAnimes);
    }

    public WatchingAnimesResponse getMyAnimesWatching(Long userId, String status, Long lastId, Integer size) {
        Long count = myPageMapper.getMyWatchCount(userId, status);
        List<WatchingAnimesDto> watchingAnimes = myPageMapper.getMyWatchingAnimes(userId, status, lastId, size);

        Long newLastId = watchingAnimes.getLast().getUserAnimeStatusId();
        CursorDto cursorDto = CursorDto.of(newLastId);

        return WatchingAnimesResponse.from(count, cursorDto, watchingAnimes);
    }

    public FinishedAnimesResponse getMyAnimesFinished(Long userId, String status, Long lastId, Integer size) {
        Long count = myPageMapper.getMyWatchCount(userId, status);
        List<FinishedAnimesDto> finishedAnimes = myPageMapper.getMyFinishedAnimes(userId, status, lastId, size);

        Long newLastId = finishedAnimes.getLast().getUserAnimeStatusId();
        CursorDto cursorDto = CursorDto.of(newLastId);

        return FinishedAnimesResponse.from(count, cursorDto, finishedAnimes);
    }

    public RatedAnimesResponse getMyAnimesRated(Long userId, Long lastId, Long lastCount, Double lastRating, Integer size, String sort, Boolean reviewOnly) {
        Long count = myPageMapper.getMyReviewCount(userId);
        SortOption sortOption = SortOption.of(sort);

        List<AnimesReviewDto> animesReviews;
        Long newLastId;
        Long newLastLikeCount;
        Double newLastRating;

        if(reviewOnly) {
            animesReviews = myPageMapper.getMyAnimesReviewsOnly(userId, lastId, size, sortOption.getCode(), lastCount, lastRating);
        } else {
            animesReviews = myPageMapper.getMyAnimesReviewsAll(userId, lastId, size, sortOption.getCode(), lastCount, lastRating);
        }

        newLastId = animesReviews.getLast().getReviewId();
        newLastLikeCount = animesReviews.getLast().getLikeCount();
        newLastRating = animesReviews.getLast().getRating();

        CursorDto cursorDto = getCursorBySortOption(newLastId, newLastLikeCount, newLastRating, sort, sortOption);

        return RatedAnimesResponse.from(count, cursorDto, animesReviews);
    }

    public LikedAnimesResponse getMyAnimesLiked(Long userId, Long lastId, Integer size) {
        Long count = myPageMapper.getMyAnimesLikeCount(userId);
        List<LikedAnimesDto> likedAnimes = myPageMapper.getMyLikedAnimes(userId, lastId, size);

        Long newLastId = likedAnimes.getLast().getAnimeLikeId();
        CursorDto cursorDto = CursorDto.of(newLastId);

        return LikedAnimesResponse.from(count, cursorDto, likedAnimes);
    }

    public LikedPersonsResponse getMyPersonsLiked(Long userId, Long lastId, Integer size) {
        Long count = myPageMapper.getMyPersonsLikeCount(userId);
        List<LikedPersonsDto> likedPersons = myPageMapper.getMyLikedPersons(userId, lastId, size);

        Long newLastId = likedPersons.getLast().getUserLikedVoiceActorId();
        CursorDto cursorDto = CursorDto.of(newLastId);

        return LikedPersonsResponse.from(count, cursorDto, likedPersons);
    }

    private CursorDto getCursorBySortOption(Long lastId, Long lastCount, Double lastRating, String sort, SortOption sortOption) {
        CursorDto cursorDto;
        switch (sortOption) {
            case LATEST:
                cursorDto = CursorDto.of(sort, lastId);
                break;
            case LIKES:
                cursorDto = CursorDto.of(sort, lastId, String.valueOf(lastCount));
                break;
            case RATING_ASC:
                cursorDto = CursorDto.of(sort, lastId, String.valueOf(lastRating));
                break;
            case RATING_DESC:
                cursorDto = CursorDto.of(sort, lastId, String.valueOf(lastRating));
                break;
            default:
                cursorDto = CursorDto.of(sort, lastId);
        }

        return cursorDto;
    }
}

