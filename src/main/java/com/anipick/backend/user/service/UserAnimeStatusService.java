package com.anipick.backend.user.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.user.domain.UserAnimeOfStatus;
import com.anipick.backend.user.domain.UserAnimeStatus;
import com.anipick.backend.user.dto.UserAnimeStatusRequest;
import com.anipick.backend.user.mapper.UserAnimeStatusMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAnimeStatusService {

    private final UserAnimeStatusMapper userAnimeStatusMapper;

    @Transactional
    public void createUserAnimeOfStatus(Long animeId, Long userId, UserAnimeStatusRequest status) {
        UserAnimeOfStatus statusEnum = status.getStatus();
        try {
            userAnimeStatusMapper.createUserAnimeStatus(userId, animeId, statusEnum);
        } catch (DuplicateKeyException e) {
            throw new CustomException(ErrorCode.ALREADY_USER_ANIME_OF_STATUS);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void updateUserAnimeOfStatus(Long animeId, Long userId, UserAnimeStatusRequest status) {
        UserAnimeOfStatus statusEnum = status.getStatus();
        UserAnimeStatus statusByUserIdAndAnimeId = userAnimeStatusMapper.findByUserId(userId, animeId);
        if (statusByUserIdAndAnimeId != null) {
            userAnimeStatusMapper.updateUserAnimeStatus(userId, animeId, statusEnum);
        } else {
            throw new CustomException(ErrorCode.USER_ANIME_OF_STATUS_DATA_NOT_FOUND);
        }
    }

    public void deleteUserAnimeOfStatus(Long animeId, Long userId) {
        UserAnimeStatus statusByUserIdAndAnimeId = userAnimeStatusMapper.findByUserId(userId, animeId);
        if (statusByUserIdAndAnimeId != null) {
            userAnimeStatusMapper.deleteUserAnimeStatus(userId, animeId);
        } else {
            throw new CustomException(ErrorCode.USER_ANIME_OF_STATUS_DATA_NOT_FOUND);
        }
    }
}
