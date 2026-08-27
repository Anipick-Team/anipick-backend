package com.anipick.backend.community.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.community.dto.PostCreateRequest;
import com.anipick.backend.community.dto.PostCreateResultDto;
import com.anipick.backend.community.dto.PostDetailDto;
import com.anipick.backend.community.dto.PostUpdateRequest;
import com.anipick.backend.community.service.CommunityPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    @PostMapping
    public ApiResponse<PostCreateResultDto> createPost(
            @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        PostCreateResultDto result = communityPostService.createPost(request, userId);
        return ApiResponse.success(result);
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostDetailDto> getPostDetail(
            @PathVariable(name = "postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        PostDetailDto result = communityPostService.getPostDetail(postId, userId);
        return ApiResponse.success(result);
    }

    @PatchMapping("/{postId}")
    public ApiResponse<Void> updatePost(
            @PathVariable(name = "postId") Long postId,
            @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        communityPostService.updatePost(postId, request, userId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
            @PathVariable(name = "postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        communityPostService.deletePost(postId, userId);
        return ApiResponse.success();
    }

    @PostMapping("/{postId}/like")
    public ApiResponse<Void> likePost(
            @PathVariable(name = "postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        communityPostService.likePost(userId, postId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{postId}/like")
    public ApiResponse<Void> unlikePost(
            @PathVariable(name = "postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        communityPostService.unlikePost(userId, postId);
        return ApiResponse.success();
    }
}
