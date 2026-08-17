package com.anipick.backend.community.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.community.dto.CommentCreateRequest;
import com.anipick.backend.community.dto.CommentCreateResultDto;
import com.anipick.backend.community.dto.CommentListPageDto;
import com.anipick.backend.community.dto.CommentUpdateRequest;
import com.anipick.backend.community.service.CommunityCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityCommentController {

    private final CommunityCommentService communityCommentService;

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<CommentListPageDto> getComments(
            @PathVariable(name = "postId") Long postId,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        CommentListPageDto result = communityCommentService.getComments(postId, lastId, size, userId);
        return ApiResponse.success(result);
    }

    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<CommentCreateResultDto> createComment(
            @PathVariable(name = "postId") Long postId,
            @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        CommentCreateResultDto result = communityCommentService.createComment(postId, request, userId);
        return ApiResponse.success(result);
    }

    @PatchMapping("/comments/{commentId}")
    public ApiResponse<Void> updateComment(
            @PathVariable(name = "commentId") Long commentId,
            @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        communityCommentService.updateComment(commentId, request, userId);
        return ApiResponse.success();
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        communityCommentService.deleteComment(commentId, userId);
        return ApiResponse.success();
    }

    @PostMapping("/comments/{commentId}/like")
    public ApiResponse<Void> likeComment(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        communityCommentService.likeComment(userId, commentId);
        return ApiResponse.success();
    }

    @DeleteMapping("/comments/{commentId}/like")
    public ApiResponse<Void> unlikeComment(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        communityCommentService.unlikeComment(userId, commentId);
        return ApiResponse.success();
    }
}
