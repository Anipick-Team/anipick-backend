package com.anipick.backend.person.dto;

import lombok.Getter;

@Getter
public class PersonInfoAllNameDto {
    private Long personId;
    private String nameKor;
    private String nameEng;
    private String profileImageUrl;
    private Boolean isLiked;
}
