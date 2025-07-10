package com.anipick.backend.user.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.user.mapper.BlockUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockUserService {

    private final BlockUserMapper blockUserMapper;

    @Transactional
    public void blockUser(Long targetUserId, Long userId) {
        Boolean isBlocked = blockUserMapper.selectBlockUser(userId, targetUserId);
        if (!isBlocked) {
            blockUserMapper.insertBlockUser(userId, targetUserId);
        } else {
            throw new CustomException(ErrorCode.ALREADY_BLOCKED_USER);
        }
    }
}
