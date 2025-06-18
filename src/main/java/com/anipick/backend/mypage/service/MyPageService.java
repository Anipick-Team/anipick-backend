package com.anipick.backend.mypage.service;

import com.anipick.backend.mypage.dto.MyPageResponse;
import com.anipick.backend.mypage.mapper.MyPageMapper;
import com.anipick.backend.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MyPageMapper myPageMapper;
    private final UserMapper userMapper;

    public MyPageResponse getMyPage(Long userId) {

    }
}
