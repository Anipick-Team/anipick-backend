package com.anipick.backend.user.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.user.service.BlockUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BlockUserController {

    private final BlockUserService blockUserService;

    @PostMapping("/{userId}/block-user")
    public ApiResponse<Void> blockUser(
            @PathVariable(value = "userId") Long targetUserId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        if (userId.equals(targetUserId)) {
            throw new CustomException(ErrorCode.SELF_BLOCKED_USER_ERROR);
        }
        blockUserService.blockUser(targetUserId, userId);
        return ApiResponse.success();
    }
}
