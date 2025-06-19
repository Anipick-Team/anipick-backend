package com.anipick.backend.mypage.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.mypage.domain.MyPageDefaults;
import com.anipick.backend.mypage.dto.LikedAnimesDto;
import com.anipick.backend.mypage.dto.LikedPersonsDto;
import com.anipick.backend.mypage.dto.MyPageResponse;
import com.anipick.backend.mypage.dto.WatchCountDto;
import com.anipick.backend.mypage.mapper.MyPageMapper;
import com.anipick.backend.user.domain.User;
import com.anipick.backend.user.domain.UserAnimeOfStatus;
import com.anipick.backend.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
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
        List<LikedAnimesDto> likedAnimesDto = myPageMapper.getMyLikedAnimes(userId, MyPageDefaults.DEFAULT_PAGE_SIZE);
        List<LikedPersonsDto> likedPersonsDto = myPageMapper.getMyLikedPersons(userId, MyPageDefaults.DEFAULT_PAGE_SIZE);

        return MyPageResponse.from(user.getNickname(), user.getProfileImageUrl(), watchCountDto, likedAnimesDto, likedPersonsDto);
    }
}
