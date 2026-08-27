package com.anipick.backend.community.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.community.domain.CommunityComment;
import com.anipick.backend.community.domain.CommunityPost;
import com.anipick.backend.community.domain.ReportCategory;
import com.anipick.backend.community.domain.ReportTargetType;
import com.anipick.backend.community.dto.ReportCreateRequest;
import com.anipick.backend.community.mapper.CommunityCommentMapper;
import com.anipick.backend.community.mapper.CommunityPostMapper;
import com.anipick.backend.community.mapper.CommunityReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityReportService {

    private final CommunityReportMapper communityReportMapper;
    private final CommunityPostMapper communityPostMapper;
    private final CommunityCommentMapper communityCommentMapper;

    @Transactional
    public void createReport(ReportCreateRequest request, Long userId) {
        ReportTargetType targetType = ReportTargetType.of(request.getTargetType());
        ReportCategory reportCategory = ReportCategory.of(request.getReportCategory());
        Long targetId = request.getTargetId();
        if (targetId == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        Long targetOwnerId = findTargetOwnerOrThrow(targetType, targetId);
        // 본인이 작성한 게시글/댓글은 신고 불가 (기존 리뷰 신고 정책과 동일)
        if (targetOwnerId.equals(userId)) {
            throw new CustomException(ErrorCode.COMMUNITY_SELF_REPORT_NOT_ALLOWED);
        }

        try {
            communityReportMapper.insertReport(userId, targetType, targetId, reportCategory);
        } catch (DuplicateKeyException e) {
            // UNIQUE(reporter_user_id, target_type, target_id)
            throw new CustomException(ErrorCode.COMMUNITY_ALREADY_REPORTED);
        }
    }

    private Long findTargetOwnerOrThrow(ReportTargetType targetType, Long targetId) {
        if (targetType == ReportTargetType.POST) {
            CommunityPost post = communityPostMapper.selectPostForCheck(targetId);
            if (post == null || post.getDeletedAt() != null) {
                throw new CustomException(ErrorCode.COMMUNITY_REPORT_TARGET_NOT_FOUND);
            }
            return post.getUserId();
        }

        CommunityComment comment = communityCommentMapper.selectCommentForCheck(targetId);
        if (comment == null || comment.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.COMMUNITY_REPORT_TARGET_NOT_FOUND);
        }
        return comment.getUserId();
    }
}
