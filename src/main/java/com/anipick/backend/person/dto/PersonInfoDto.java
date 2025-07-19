package com.anipick.backend.person.dto;

import lombok.Getter;

@Getter
public class PersonInfoDto {
    private Long personId;
    private String name;
    private String profileImageUrl;
    private Boolean isLiked;
}
