package com.anipick.backend.image.domain;

/**
 * image.image_type 컬럼 값. DB 에는 name() 그대로 저장된다(EnumTypeHandler).
 * - PROFILE: 유저 프로필 이미지(유저당 1행 유지)
 * - COMMUNITY_POST: 커뮤니티 게시글 첨부 이미지(유저당 다수 가능)
 */
public enum ImageType {
    PROFILE,
    COMMUNITY_POST
}
