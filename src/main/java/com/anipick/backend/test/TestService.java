package com.anipick.backend.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestMapper testMapper;

    public List<TestResponseDto> findReviews(long userId) {
        return testMapper.findReviews(userId);
    }
}
