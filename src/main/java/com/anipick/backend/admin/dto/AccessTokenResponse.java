package com.anipick.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class AccessTokenResponse {
    private String accessToken;
}
