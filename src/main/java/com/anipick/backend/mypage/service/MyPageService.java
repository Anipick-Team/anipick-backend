package com.anipick.backend.mypage.service;

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
    private final DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public MyPageResponse getMyPage(Long userId) {
        User user = userMapper.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_BY_EMAIL));

        Long watchListCount = myPageMapper.getMyWatchCount(userId, UserAnimeOfStatus.WATCHLIST.toString());
        Long watchingCount = myPageMapper.getMyWatchCount(userId, UserAnimeOfStatus.WATCHING.toString());
        Long finishedCount = myPageMapper.getMyWatchCount(userId, UserAnimeOfStatus.FINISHED.toString());

        WatchCountDto watchCountDto = WatchCountDto.from(watchListCount, watchingCount, finishedCount);
        List<LikedAnimesDto> likedAnimesDto = myPageMapper.getMyLikedAnimes(userId, MyPageDefaults.DEFAULT_PAGE_SIZE);
        List<LikedPersonsDto> likedPersonsDto = myPageMapper.getMyLikedPersons(userId, MyPageDefaults.DEFAULT_PAGE_SIZE);

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
}
