package com.anipick.backend.user.dto;

import com.anipick.backend.user.domain.UserAnimeOfStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAnimeStatusRequest {
    private UserAnimeOfStatus status;
}
