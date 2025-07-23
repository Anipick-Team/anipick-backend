package com.anipick.backend.user.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.user.mapper.BlockUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockUserService {

    private final BlockUserMapper blockUserMapper;

    @Transactional
    public void blockUser(Long targetUserId, Long userId) {
        try {
            blockUserMapper.insertBlockUser(userId, targetUserId);
        } catch (DuplicateKeyException e) {
            throw new CustomException(ErrorCode.ALREADY_BLOCKED_USER);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
